#!/bin/bash
set -euo pipefail
exec > >(tee /var/log/umc-boot.log) 2>&1

dnf install -y docker
systemctl enable --now docker

# 1. 배포 스크립트 생성
mkdir -p /opt/umc
cat > /opt/umc/swap.sh <<'SWAP'
#!/bin/bash
set -euo pipefail

REGION=ap-southeast-2

p() {
  aws ssm get-parameter \
    --region "$REGION" \
    --name "/umc/app/$1" \
    --with-decryption \
    --query Parameter.Value \
    --output text
}

# Docker Hub 이미지 다운로드
IMAGE=$(p image)
docker pull "$IMAGE"

# 기존 컨테이너 제거
docker rm -f app >/dev/null 2>&1 || true

# Spring Boot 컨테이너 실행
docker run -d --name app --restart unless-stopped -p 8080:8080 \
  -e DB_URL="jdbc:mysql://$(p db-host):3306/$(p db-name)" \
  -e DB_USER="$(p db-user)" \
  -e DB_PASSWORD="$(p db-password)" \
  -e JWT_SECRET="$(p jwt-secret)" \
  -e JWT_ACCESS_EXPIRATION=3600 \
  "$IMAGE"
SWAP

chmod +x /opt/umc/swap.sh

# 2. 부팅 때 한 번 실행
/opt/umc/swap.sh

# 3. Alloy Config
# 2주차 워크북에서 추가
