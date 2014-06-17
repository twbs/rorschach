#!/usr/bin/env python2.7
# coding=utf-8
from __future__ import absolute_import, unicode_literals, division, print_function
from hmac import compare_digest, new as hmac
from hashlib import sha1
from functools import wraps
from flask import Flask, request, Response
from werkzeug.exceptions import Forbidden
# from rorschach.huey import huey

app = application = Flask(__name__)

def verify_hub_signature(func):
    @wraps(func)
    def verified(**kwargs):
        given_sig = request.headers['X-Hub-Signature']
        correct_sig = hmac("MY-KEY", msg=request.data, digestmod=sha1).hexdigest()
        if compare_digest(given_sig, correct_sig):
            return func(**kwargs)
        else:
            app.logger.error("Invalid signature; expected {}; got {}".format(correct_sig, given_sig))
            raise Forbidden(description="Invalid HMAC-SHA1")
    return verified


@app.route('/rorschach/')
@verify_hub_signature
def verify_deserialize_and_enqueue():
    if request.headers['X-Github-Event'] == 'pull_request':
        pull_req_payload = request.get_json()
        app.logger.info("Got payload: {!r}".format(pull_req_payload))
    else:
        app.logger.info("Ignoring irrelevant event")
    return Response(b"OK", content_type=b'text/plain; charset=UTF-8')


if __name__ == '__main__':
    app.run()
