# mrb
meeting room booking

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
