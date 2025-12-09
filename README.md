# DeepScope

## 프로젝트 소개
> 게시판 커뮤니티

- 프로젝트 참여 인원: 1명(개인 프로젝트)  
- 기간: 2025.9.8~2025.12.9  
- 기술의 사용 이유와 트레이드오프에 집중하고, 클라우드 네이티브한 환경을 목표로 합니다.

## ERD
<img width="800" height="462" alt="Image" src="https://github.com/user-attachments/assets/e525a44f-4d98-4da3-a8ba-5b32dac75869" />

## API 명세서

## 시스템 아키텍처
<img width="800" height="918" alt="Image" src="https://github.com/user-attachments/assets/f7cc1fc9-cf72-4d63-aa8c-178bdba2a5f8" />

## 레포지토리 구조
<img width="387" height="815" alt="Image" src="https://github.com/user-attachments/assets/3e0de223-987d-4332-a1d1-cf9ee613483d" />

## 기술 스택
### 백엔드
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)

### DB
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

### AWS
![AWS EC2](https://img.shields.io/badge/Amazon%20EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white)
![AWS RDS](https://img.shields.io/badge/Amazon%20RDS-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white)
![AWS API Gateway](https://img.shields.io/badge/Amazon%20API%20Gateway-FF4F8B?style=for-the-badge&logo=amazonapigateway&logoColor=white)
![AWS Lambda](https://img.shields.io/badge/AWS%20Lambda-FF9900?style=for-the-badge&logo=awslambda&logoColor=white)
![AWS S3](https://img.shields.io/badge/Amazon%20S3-569A31?style=for-the-badge&logo=amazons3&logoColor=white)
![AWS CloudFront](https://img.shields.io/badge/Amazon%20CloudFront-485672?style=for-the-badge&logo=amazoncloudfront&logoColor=white)
![AWS VPC](https://img.shields.io/badge/Amazon%20VPC-FF4F8B?style=for-the-badge&logo=amazonvpc&logoColor=white)
![AWS ECR](https://img.shields.io/badge/Amazon%20ECR-FF9900?style=for-the-badge&logo=amazonelasticcontainerregistry&logoColor=white)

### CI/CD
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

## 기술적 의사결정
### 1. Redis 사용 여부
JWT 기반 인증 시스템에서 Refresh Token을 저장할 때, Redis와 RDBMS 중 어느 것을 사용할지 결정이 필요
#### 분석
1. DB 요청 처리 성능
   - Refresh Token 조회는 Access Token 재발급 시에만 발생하고, Access Token 만료는 1시간으로 설정
   - 100만 MAU를 가정했을 때, 예상 재발급 TPS는 피크 시간 기준 약 10
   - RDS db.t3.small (gp3) 스펙은 IOPS 3000을 지원하고, 실제 처리 성능은 쿼리 복잡도에 따라 QPS 100~200 수준
   - 100만 MAU를 가정했을 때 통상적으로 커뮤니티 서비스의 DAU는 20%로 하루 20만명이 이용한다고 가정할 수 있고, 1인당 하루 평균 API 호출을 20회 한다고 가정하면 평균 QPS는 200,000 * 20 / 86,400 = 46 QPS
   - 피크타임의 QPS는 5배라고 가정하면 최대 QPS는 230
   - Refresh Token은 인덱스 조회로 단순하고, 캐시 히트율이 높아 피크 230 QPS도 IOPS 3000 범위 내에서 처리 가능
2. TTL 관리
   - JWT의 exp claim을 통해 애플리케이션 레벨에서 검증하므로, Redis의 자동 만료 기능 없이도 처리 가능
3. 읽기 부하
   - 읽기 부하는 RDS Read Replica를 통해 분산
#### 결정
현재 예상 부하에서는 RDBMS만으로도 안정적인 처리가 가능하다고 판단했고, RDS + Read Replica 구조로 확장성을 확보해 Redis를 사용하지 않기로 선택
(이후 트러블슈팅 과정에서 Read Replica 설정 변경 - 트러블슈팅 섹션 참고)

### 2. 복합키의 클러스터 인덱스
좋아요(PostLike)테이블에서 사용자가 같은 게시글에 중복으로 좋아요를 생성하는 것을 방지하기 위해 (post_id, user_id)를 복합 Primary Key로 설계

#### 문제
- 클러스터 인덱스는 PK 순서로 물리적 데이터를 정렬
- (post_id, user_id) 복합키는 post_id 우선 정렬 방식
- 여러 게시글에 좋아요가 무작위로 발생하여 삽입 순서가 불규칙하므로 페이지 분할이 빈번하게 발생
- 좋아요는 생성/삭제가 빈번한 작업으로 성능 영향이 클 것이라 판단

#### 결정
- 비식별 관계로 변경
- id (auto increment)를 Primary Key로 설정하여 순차 증가로 삽입 최적화
- (post_id, user_id)는 UNIQUE 인덱스로 변경하여 중복 방지 기능 유지

### 3. Spring Security + JWT 사용
인증 방식으로 세션 기반과 JWT 기반, 보안 프레임워크 사용 여부를 고려

#### 분석
1. 세션 + 필터 방식
   - 세션 ID만 전송하여 데이터 노출 최소화
   - API 요청마다 세션 조회 필요 → DB 조회 2배 증가
   - Redis 없이 RDBMS로 세션 관리 시 부하 증가
   - Redis 도입 시 추가 비용 발생
2. JWT + 필터 방식
   - Stateless 방식으로 요청마다 DB 조회 불필요
   - 의존성과 필터 체인 오버헤드 최소화
   - CORS, XSS, CSRF 등 보안 설정 직접 구현 필요
   - 예외 처리 등 세부 기능 구현 필요
3. JWT + Spring Security 방식
   - CORS, CSRF, 인증/인가 필터 등 기본 제공
   - 보안 설정의 표준화
   - 다수의 기본 필터 체인으로 인한 오버헤드

#### 결정
추가적인 필터 처리 오버헤드보다 검증된 보안 설정의 이점이 크다 판단해 JWT + Spring Security 방식 선택

### 4. 멀티 AZ 구성
클라우드 네이티브 환경에서 고가용성 확보를 위한 인프라 설계 필요

#### 고려사항
1. Single AZ
   - AZ 장애 시 전체 서비스 중단
2. Multi AZ - Active/Passive
   - 장애 발생 후 Failover 방식 -> 서비스 다운타임 발생
3. Multi AZ - Active/Active
   - 평상시에도 트래픽 분산 -> 리소스 효율적 사용 가능
   - 장애 시 즉시 나머지 AZ로 처리

#### 결정
Active/Active 멀티 AZ 구성 + 3-Tier 아키텍처 선택

#### 구조
- Public Subnet: Frontend (Express 서버) - 리버스 프록시 역할
- Private Subnet: Backend (Spring Boot WAS)
- Private Subnet: Database (RDS Multi-AZ)

#### 결과
- 두 개 이상의 AZ에 각 계층 인스턴스 배치하여 트래픽 분산
- 오토스케일링 없이도 현재 리소스를 효율적으로 활용
- AZ 장애 시 다운타임 없이 정상 AZ에서 즉시 처리
- WAS를 Private Subnet에 배치하고 Express 프록시를 통해서만 접근 가능하도록 하여 외부 직접 노출 차단

### 5. 이미지 처리 방식: Presigned URL + CloudFront
게시글 작성, 프로필 이미지 등록 시 이미지 업로드 처리 방식 결정 필요

#### 아키텍처
1. 클라이언트 → API Gateway → Lambda: 업로드 URL 요청
2. Lambda → S3: Presigned URL 생성 후 응답
3. 클라이언트 → S3: Presigned URL로 직접 업로드
4. 클라이언트 → WAS: 게시글/회원가입 요청 시 이미지 URL을 DTO에 포함하여 DB 저장
5. 이미지 조회: CloudFront를 통해 캐싱된 이미지 제공

#### 고려사항
1. 서버 직접 업로드 방식
   - WAS가 이미지를 받아서 S3로 업로드하면 네트워크 이중 부하 발생
   - WAS 트래픽 증가 및 업로드 처리 시간 증가
   - API Gateway 페이로드 제한 10MB로 다른 요청 처리에 영향
2. Lambda 직접 업로드 방식
   - 동시 업로드 요청 시 Lambda 컨테이너가 계속 증가하여 비용 증가
   - API Gateway 페이로드 제한 10MB로 다른 요청 처리에 영향
3. Presigned URL 방식
   - Lambda는 가벼운 URL 생성만 담당
   - 클라이언트가 S3에 직접 업로드하여 서버 부하 없음
   - 요청이 2회(URL 요청 + 업로드)로 증가하지만 URL 생성은 매우 빠르고, 실제 업로드는 클라이언트에서 처리해 사용자의 체감 속도와 서버 부하에 영향이 없음
4. CloudFront 도입
   - 이미지 조회 시마다 S3 엔드포인트 노출은 보안 취약
   - 반복 조회 시 캐시에서 응답

#### 결과
Presigned URL 방식 + CloudFront 도입 결정
- 서버 네트워크 부하 최소화
- Lambda 비용 효율화
- 이미지 조회 성능 향상
- S3 직접 노출 차단으로 보안 강화

## 트러블 슈팅
### 1. 읽기 전용 replica DB 문제
#### 문제
멀티 AZ 환경에서 게시글 상세 조회 API 호출 시 간헐적으로 500 서버 에러 발생

#### 초기 가설
Replica DB에 데이터가 복제되기 전에 조회 요청이 발생하여 문제가 생긴다고 판단
-> 그러나 이 경우 500이 아닌 404 에러가 발생해야 함

#### 원인 분석
로그를 확인한 결과 실제 원인 파악
- AZ-A RDS: Master DB (읽기 + 쓰기 가능)
- AZ-B RDS: Read Replica (읽기 전용)
- 게시글 상세 조회 시 조회수가 1 증가하는 로직 존재
- 조회 요청임에도 불구하고 조회수 업데이트라는 쓰기 작업이 포함

#### 에러 발생 시나리오
1. 게시글 상세 조회 요청 → ALB → AZ-B의 WAS로 라우팅
2. AZ-B WAS → AZ-B Read Replica에 조회 요청
3. 조회 중 조회수 업데이트 시도
4. Read Replica는 읽기 전용 → 쓰기 거부 → 500 에러 발생

#### 해결 방법
Read Replica 제거 후 모든 WAS가 AZ-A의 Master DB로 요청하도록 구성 변경

#### 결정 근거
- Master DB 단독으로도 QPS 100~200 수준, IOPS 3000 범위 내에서 100만 MAU 기준 피크 QPS 230을 충분히 처리할 수 있음

#### 결과
- 500 에러 해결
- 모든 조회 요청이 Master DB에서 정상 처리

#### 개선 고려사항
- DB 부하가 높아질 경우 조회수 업데이트 로직을 비동기 처리로 분리
- Read Replica 재도입 후, 쓰기 작업이 포함된 읽기 요청(게시글 상세 조회 요청)을 애플리케이션 레벨에서 Master DB로 라우팅

## 시연 영상

---
### 프론트엔드 레포지토리
https://github.com/kkangssu/3-jina-kang-community-FE
