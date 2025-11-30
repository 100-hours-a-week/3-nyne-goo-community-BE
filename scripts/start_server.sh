#!/bin/bash
set -e # error 시 즉시 종료

ECR_REGISTRY=586421527844.dkr.ecr.ap-northeast-2.amazonaws.com
echo "[deploy.sh] start"

APP_DIR="$HOME/dorandoran-be"

# BE 서버에 docker-compose.yml이 있는 디렉토리로 이동
cd "$APP_DIR"

# ECR 로그인
echo "[deploy.sh] ECR login..."
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin $ECR_REGISTRY

docker compose pull
docker compose up -d

echo "[deploy.sh] done"