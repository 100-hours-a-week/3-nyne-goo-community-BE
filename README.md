# Dorandoran Community - Backend
도란도란 커뮤니티 백엔드는 서비스의 핵심 비즈니스 로직, 사용자 인증, 게시글 및 관리, 이미지 메타데이터 저장을 담당합니다.
이미지 업로드는 백엔드가 직접 처리하지 않고 프론트엔드 -> API Gateway -> Lambda -> S3 흐름으로 처리되며 백엔드는 업로드된 S3 이미지 경로만 DB에 저장하도록 설계되었습니다.

## 프로젝트 개요
도란도란은 사용자들이 일상을 나누고 소통할 수 있는 커뮤니티 플랫폼입니다.
- 도메인 기반의 명확한 책임 분리
- AWS 기반의 안정적 인프라 운영
- 빠른 응답성을 위한 조회수, 좋아요, 댓글수 최적화
- 프론트엔드, Lambda와의 분리된 책임 구조 확립

## 핵심 기능
도란도란 커뮤니티 백엔드는 보안 중심 네트워크 구조, AWS 기반 이미지 처리 파이프라인, 효율적인 배포 및 데이터 캐싱 전략을 목표로 설계되었습니다.

### 1. Private Subnet 기반 백엔드 및 프론트엔드 인프라
사용자 요청은 다음 흐름으로 처리됩니다.
Browser -> Internet Gateway -> Nginx(Public Subnet) -> Backend or Frontned (Private Subnet)
- 비용 절감을 위해 ALG 대신 Nginx Reverse Proxy 사용
- WAS, WS 모두 Private Subnet에 배치해 직접 인터넷 노출 차단
- Backend -> RDS(MySQL)로 데이터 처리

### 2. 이미지 처리 업로드 & 제공 구조
이미지 업로드는 백엔드가 아닌 API Gateway -> Lambda -> S3가 담당합니다.
백엔드는 업로드된 이미지 경로만 DB에 저장합니다.
이미지 조회는 S3 대신 CloudFront 를 통해 제공해 S3 Private 버킷의 403 문제를 해결하고 성능 향상과 보안 강화를 하였습니다.

### 3. CI/CD 전략
백엔드와 프론트엔드 서버가 Private Subnet에 있어 Github Actions 에서 직접 배포가 불가능했습니다.
이에 따라 CI는 Github Actions, CD는 AWS CodeDeploy 구조로 구성했습니다.

### 4. 테스트 환경 분리
CI 단계의 테스트는 H2 인메로리 DB로 실행했습니다.
- 실제 MySQL 건드리는 위험 제거
- 빠른 테스트 속도 확보
- 운영 환경(RDS MySQL)과 명확한 분리

### 5. 인메모리 기반 카운팅 최적화
조회수, 좋아요수, 댓글수는 인메모리 캐시(HashMap)에 저장 후 1분마다 배치 업데이트 방식으로 DB에 반영합니다.
- DB 부하 감소
- 빠른 조회 성능
- Redis 없이 캐싱 효과 구현


## 아키텍처 구성도
<img width="585" height="708" alt="Screenshot 2025-12-09 at 13 34 56" src="https://github.com/user-attachments/assets/d53f58c1-1896-474a-95a2-5e98757b1781" />



