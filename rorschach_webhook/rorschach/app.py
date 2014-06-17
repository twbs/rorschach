#!/usr/bin/env python2.7
# coding=utf-8
from __future__ import absolute_import, unicode_literals, division, print_function
from hmac import new as hmac
from hashlib import sha1
from functools import wraps
from flask import Flask, request, Response
from werkzeug.exceptions import Forbidden, BadRequest
# from rorschach.huey import huey

RELEVANT_ACTIONS = frozenset('opened' 'synchronize')

app = application = Flask(__name__)

try:
    from hmac import compare_digest
except ImportError:
    def compare_digest(left, right):
        diff = 0
        for l, r in zip(left, right):
            diff |= ord(l) ^ ord(r)
        return diff == 0

def verify_hub_signature(func):
    @wraps(func)
    def verified(**kwargs):
        malformed = False
        try:
            sha_one, given_sig = request.headers['X-Hub-Signature'].split('=')
        except ValueError:
            malformed = True
        else:
            malformed = (sha_one != 'sha1')
        if malformed:
            raise BadRequest(description="HMAC-SHA1 header value was malformed")

        correct_sig = hmac(b"MY-KEY", msg=request.data, digestmod=sha1).hexdigest()
        if compare_digest(given_sig, correct_sig):
            return func(**kwargs)
        else:
            app.logger.error("Invalid signature; expected {}; got {}".format(correct_sig, given_sig))
            raise Forbidden(description="Invalid HMAC-SHA1")
    return verified


@app.route('/rorschach/', methods=['POST'])
@verify_hub_signature
def verify_deserialize_and_enqueue():
    event = request.headers['X-Github-Event']
    if event == 'pull_request':
        pull_req_payload = request.get_json()
        if not isinstance(pull_req_payload, dict):
            raise BadRequest("JSON does not match expected schema")
        app.logger.info("Got payload: {!r}".format(pull_req_payload))
        if pull_req_payload.get('action') not in RELEVANT_ACTIONS:
            app.logger.info("Ignoring irrelevant action")
    elif event == 'ping':
        app.logger.info("Successfully received ping event from GitHub")
    else:
        app.logger.info("Ignoring irrelevant event")
    return Response(b"OK", content_type=b'text/plain; charset=UTF-8')


if __name__ == '__main__':
    app.run()
