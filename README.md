# 회의실 예약 웹 어플리케이션

## 요구사항에 따른 구성 방안
### 1. 입력 파라미터
```java
public class BookRequestParam {
  // 시작일
  private String startDate;
  // 종료일
  private String endDate;
  // 반복, 반복되지 않으면 null 을 나타낸다.
  private String cronExpression;
  // 시작시간
  private int startTime;
  // 종료시간
  private int endTime;
}
```
### 설명
##### 시작일(문자열: YYYYMMDD)
> 2019년 1월 30일 일 경우
```
20190130
```
##### 종료일(문자열: YYYYMMDD), 하루만 진행하면 종료일은 시작일과 같다.
> 2019년 11월 30일 일 경우
```
20191130
```
##### 반복(문자열: Cron expression)
> 월요일만 반복할 경우, 
```
0 0 0 ? * MON *
```
##### 시작시간(정수형: 0~47) 
> 0일 경우 0시 0분, 1일 경우, 0시 30분과 같이 30분단위로 증가함
##### 종료시간(정수형: 0~47, 시작시간보다 커야함)
> 0일 경우 0시 0분, 1일 경우, 0시 30분과 같이 30분단위로 증가함

### 2. 데이터 베이스
#### 데이터베이스를 통해서 관리되는 정보는 아래와 같다.
- **요청 파라미터** : 예약 내용을 조회, 변경할때 사용
- **일별 예약일** : 일별 예약 정보를 조회할때 사용
- **코드(타임코드, 회의실코드)** : 파라미터에 대한 설명이나 브라우저에 전달하여 브라우저가 테이블을 그릴때 표출

## 개발 및 빌드 환경
- Rest API Server: JAVA 1.8, Spring Boot 2.1, H2 DB, Tomcat container
- Angular 7, Angular material
- Gradle 5.1

## 실행 방법
1. https://github.com/KimSeokWon/mrb.git 에서 소스를 다운 받는다.
2. 단위 테스트를 진행한다.
```aidl
%YOUR_PATH%> gradle test
```
3. 소스를 다운 받은 위치로 이동하여 아래와 같이 빌드 및 실행한다. 
```
%YOUR_PATH%> gradle bootRun 
```
4. 브라우저에서 http://localhost:8080 으로 실행한다.

### 참고사항
웹소스는 ./mrb-web 에 있다. 해당 디렉토리로 들어가서 
```aidl
CL> npm install // libaaries installation 
C:> ng build --prod 
```
와 같이 프로덕션 모드로 빌드를 하면 되며, 빌드된 파일은 ```./mrb-web/dist/mrb-web``` 에 존재하며 모든 파일을 ```./src/main/resources/META-INF/resources``` 하위에 복사하면 된다.
또한, 빌드 하기 위해서는 **node.js**, **@angular/cli** 가 설치되어 있어야 한다.
