import {Injectable} from "@angular/core";
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {NGXLogger} from "ngx-logger";
import {environment} from "../../environments/environment";
import {MeetingRoom} from "../model/meeting-room";
import {Observable, throwError, of} from "rxjs";
import {TimeTable} from "../model/time-table";
import {BookingParam} from "../model/booking-param";
import {catchError, map, tap} from "rxjs/operators";
import {InitParam} from "../model/init-param";


const httpPostOption = {
  headers: new HttpHeaders({
    'Access-Control-Allow-Origin': 'http://localhost:8080',
    'Access-Control-Allow-Methods': 'GET, POST, DELETE',
    'Access-Control-Allow-Credentials': 'true',
    'Content-Type': 'application/json'
  })
}

const httpGetOption = {
  headers: new HttpHeaders({
    'Access-Control-Allow-Origin': 'http://localhost:8080',
  })
}

@Injectable()
export class ServerProtocolService {
  constructor(
    private http: HttpClient,
    private logger: NGXLogger
  ){
    logger.debug(this.makeBaseURL());
  }

  private makeBaseURL(): string {
    return environment.server.protocol + '://' + environment.server.hostname + ':' + environment.server.port + environment.server.basePath;
  }

  getMeetingRooms(): Observable<MeetingRoom[]> {
    if ( localStorage.getItem("meeting-room") ) {
        return of(JSON.parse(localStorage.getItem("meeting-room")));
    } else {
      return this.http.get<MeetingRoom[]>(
        this.makeBaseURL() + '/select/meeting-room', httpGetOption ).pipe(
        tap( data => {
          localStorage.setItem('meeting-room', JSON.stringify(data));
        })
      );
    }
  }

  getTimeTable(): Observable<TimeTable[]> {
    if ( localStorage.getItem("time-table") ) {
      return of(JSON.parse(localStorage.getItem("time-table")));
    } else {
      return this.http.get<TimeTable[]>(
        this.makeBaseURL() + '/select/time-table', httpGetOption).pipe(
          tap( data => {
            localStorage.setItem('time-table', JSON.stringify(data));
          })
      );
    }
  }

  getBookingList(date: string): Observable<any[]> {
    return this.http.get<any[]>(
      this.makeBaseURL() + '/select/' + date, httpGetOption );
  }
  postBookingParam(param: BookingParam): Observable<any> {
    return this.http.post<BookingParam>( this.makeBaseURL() + '/create', param, httpPostOption ).pipe( catchError(this.handleError) );
  }

  getBookingParam(reqId: number): Observable<BookingParam> {
    return this.http.get<BookingParam>(
      this.makeBaseURL() + '/select/detail/' + reqId, httpGetOption);
  }

  modifyBooingParam(param: BookingParam): Observable<any> {
    return this.http.post<BookingParam>( this.makeBaseURL() + '/modify', param, httpPostOption ).pipe( catchError(this.handleError) );
  }

  removeBookingInfo(reqId: number): Observable<any> {
    return this.http.delete<BookingParam>(this.makeBaseURL() + '/remove/' + reqId, httpGetOption).pipe(catchError(this.handleError));
  }

  public getInitStatus(): Observable<any> {
    return this.http.get(this.makeBaseURL() + '/check-status', );
  }
  public sendInitParam(param: InitParam): void {
    this.http.post<InitParam>(
        this.makeBaseURL() + '/init', param, httpPostOption
    ).pipe(catchError(this.handleError));
  }

  public handleError(error: HttpErrorResponse) {
    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      console.error('An error occurred:', error.error.message);
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong,
      /*console.error(
        `Backend returned code ${error.status}, ` +
        `body was: ${error.error}`);*/
      console.log(error.error);

      return (error.error && error.error.message) ? throwError(error.error.message) : throwError('Server Error');

    }
    // return an observable with a user-facing error message
    return throwError(
      'Something bad happened; please try again later.');
  };

}
