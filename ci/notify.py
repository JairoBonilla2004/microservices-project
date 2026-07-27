#!/usr/bin/env python3
import os
import sys

import requests


def emoji(status: str) -> str:
    s = status.upper()
    if s == "SUCCESS":
        return "\u2705"
    if s in ("FAILURE", "CANCELLED"):
        return "\u274c"
    return "\u23ed\ufe0f"


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

    build_err = os.environ.get("_BUILD_ERROR", "")
    sonar_err = os.environ.get("_SONAR_ERROR", "")
    sast_err = os.environ.get("_SAST_ERROR", "")
    docker_err = os.environ.get("_DOCKER_ERROR", "")
    deploy_err = os.environ.get("_DEPLOY_ERROR", "")

    coverage = os.environ.get("_COVERAGE", "")
    overall = "SUCCESS"
    for s in (build, sonar, sast, docker, deploy):
        if s in ("FAILURE", "CANCELLED"):
            overall = s
            break

    branch = os.environ.get("_BRANCH", "?")
    commit = os.environ.get("_COMMIT", "?")
    event = os.environ.get("_EVENT", "?")
    repo = os.environ.get("_REPO", "?")
    run_id = os.environ.get("_RUN_ID", "?")
    server_url = os.environ.get("_SERVER_URL", "https://github.com")

    overall_icon = "\u2705" if overall == "SUCCESS" else "\u274c"

    lines = [
        f"<b>{overall_icon} Pipeline Master Gateway</b>\n",
        f"<b>Branch:</b> {branch}",
        f"<b>Commit:</b> <code>{commit[:7]}</code>",
        f"<b>Evento:</b> {event}\n",
        "<b>Resultados:</b>",
    ]

    steps = [
        ("\U0001f528 Build & Test", build, build_err),
        ("\U0001f52e SonarCloud", sonar, sonar_err),
        ("\U0001f916 SAST ML", sast, sast_err),
        ("\U0001f433 Docker Push", docker, docker_err),
        ("\U0001f680 GitOps Deploy", deploy, deploy_err),
    ]

    overall_failed = False
    for label, status, err in steps:
        line = f"  {emoji(status)} <b>{label}:</b> {status}"
        if err:
            line += f"\n  <code>{err[:300]}</code>"
            overall_failed = True
        lines.append(line)

    if coverage:
        lines.append(f"\n\U0001f4ca <b>Cobertura Frontend:</b> {coverage}")

    if overall_failed:
        lines.append(f'\n\U0001f6a8 <b>El pipeline fall\u00f3.</b> Revisa los logs para m\u00e1s detalles.')

    lines.append(
        f'\n<a href="{server_url}/{repo}/actions/runs/{run_id}">'
        "\U0001f4cb Ver pipeline</a>"
    )

    msg = "\n".join(lines)

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
