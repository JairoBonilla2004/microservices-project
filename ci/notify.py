#!/usr/bin/env python3
"""
Script de notificación para el pipeline CI/CD.
Envía el resumen del pipeline a Telegram.
"""

import os
import sys

import requests


def main():
    token = os.environ.get("TELEGRAM_BOT_TOKEN", "")
    chat_id = os.environ.get("TELEGRAM_CHAT_ID", "")

    if not token or not chat_id:
        print("TELEGRAM_BOT_TOKEN or TELEGRAM_CHAT_ID not set")
        sys.exit(0)

    build = os.environ.get("_BUILD", "?").upper()
    sonar = os.environ.get("_SONAR", "?").upper()
    sast = os.environ.get("_SAST", "?").upper()
    docker = os.environ.get("_DOCKER", "?").upper()
    deploy = os.environ.get("_DEPLOY", "?").upper()

    branch = os.environ.get("_BRANCH", "?")
    commit = os.environ.get("_COMMIT", "?")
    event = os.environ.get("_EVENT", "?")
    repo = os.environ.get("_REPO", "?")
    run_id = os.environ.get("_RUN_ID", "?")
    server_url = os.environ.get("_SERVER_URL", "https://github.com")

    msg = (
        "<b>\U0001f50d Pipeline Master Gateway</b>\n\n"
        f"<b>Branch:</b> {branch}\n"
        f"<b>Commit:</b> <code>{commit[:7]}</code>\n"
        f"<b>Evento:</b> {event}\n\n"
        "<b>Resultados:</b>\n"
        f"\U0001f528 Build & Test:  {build}\n"
        f"\U0001f52e SonarCloud:    {sonar}\n"
        f"\U0001f916 SAST ML:       {sast}\n"
        f"\U0001f433 Docker Push:   {docker}\n"
        f"\U0001f680 GitOps Deploy: {deploy}\n\n"
        f'<a href="{server_url}/{repo}/actions/runs/{run_id}">'
        "\U0001f4cb Ver pipeline</a>"
    )

    r = requests.post(
        f"https://api.telegram.org/bot{token}/sendMessage",
        json={
            "chat_id": chat_id,
            "text": msg,
            "parse_mode": "HTML",
            "disable_web_page_preview": True,
        },
        timeout=15,
    )
    print(f"Telegram: {r.status_code} {'OK' if r.ok else r.text}")


if __name__ == "__main__":
    main()
