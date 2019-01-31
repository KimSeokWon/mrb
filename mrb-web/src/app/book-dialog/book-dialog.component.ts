import {Component, Inject, OnInit} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDatepickerInputEvent,
  MatDialogRef, MatSnackBar
} from "@angular/material";

import {NGXLogger} from "ngx-logger";
import {ServerProtocolService} from "../service/server-protocol.service";
import {Observable, throwError} from "rxjs";
import {TimeTable} from "../model/time-table";
import {BookingParam} from "../model/booking-param";
import {MeetingRoom} from "../model/meeting-room";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {catchError} from "rxjs/operators";
import {error} from "util";
import {HttpErrorResponse} from "@angular/common/http";
import * as moment from 'moment';

@Component({
  selector: 'app-book-dialog',
  templateUrl: './book-dialog.component.html',
  styleUrls: ['./book-dialog.component.scss'],
})
export class BookDialogComponent implements OnInit {
  bookingDate: string;
  timeTable$: Observable<TimeTable[]>;
  meetingRooms$: Observable<MeetingRoom[]>;
  form: FormGroup;
  bookingParam: BookingParam;
  errorIsShown = false;
  errorMsg ='';

  dialogTitle = '회의실 예약';

  week_code: string[] = [
    'SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'
  ];

  primary_colors: string[] = [
    '#6200EE', '#03DAC6', '#018786', '#B00020', '#FFED03', '#FF0266'
  ];

  constructor(
    public dialogRef: MatDialogRef<BookDialogComponent>,
    private fb: FormBuilder,
    private logger: NGXLogger,
    private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) public data: BookingParam,
    @Inject(ServerProtocolService) private serverProtocolService,
  ) {
    if ( data == null ) {
      this.form = fb.group({
        desc: ['', [Validators.required, Validators.minLength(2)]],
        room: [0, Validators.required],
        tt: [0, Validators.required],
        dur: [0, Validators.required],
        cron: [''],
        color: ['#6200EE'],
        from: ['', Validators.required],
        to: ['', Validators.required]
      });
    } else {
      this.bookingParam = data;
      this.dialogTitle = '회의실 변경';
      console.log(data);
      this.logger.debug(data.cronExpression);
      this.logger.debug(data.cronExpression ? data.cronExpression.split(' ')[5].split(',') : '');
      this.form = fb.group({
        desc: [data.description, [Validators.required, Validators.minLength(2)]],
        room: [data.meetingRoomId, Validators.required],
        tt: [data.startTime, Validators.required],
        dur: [data.duration, Validators.required],
        cron: [data.cronExpression ? data.cronExpression.split(' ')[5].split(',') : ''],
        color: [data.color],
        //from: [moment(data.from, "YYYYMMDD").toDate(), Validators.required],
        from: [new Date(data.fromDate), Validators.required],
        to: [new Date(data.toDate), Validators.required]
      });
    }
  }

  ngOnInit() {
    this.errorIsShown = false;
    this.timeTable$ = this.serverProtocolService.getTimeTable();
    this.meetingRooms$ = this.serverProtocolService.getMeetingRooms();

  }
  changeCalendarEvent(action: string, event: MatDatepickerInputEvent<Date>) {
    this.logger.debug(event.value);
  }
  save() {
    this.errorIsShown = false;
    if ( this.data == null ) {
      this.bookingParam = new BookingParam(
        this.format(this.form.get('from').value),
        this.format(this.form.get('to').value  ? this.form.get('to').value : this.form.get('from').value),
        this.form.get('tt').value,
        this.form.get('dur').value,
        this.form.get('desc').value,
        ((this.form.get('from').value == this.form.get('to').value) || !this.form.get('to').value)  ? null : "0 0 0 ? * " + this.form.get('cron').value + ' *',
        this.form.get('room').value
      );
      this.bookingParam.color = this.form.get('color').value;
      //this.logger.debug(JSON.stringify(this.bookingParam));
      this.serverProtocolService.postBookingParam(this.bookingParam).
      subscribe( data => {
        this.openSuccess('Success to save')
      }, error => {
        console.log(error);
        this.errorIsShown = true;
        this.errorMsg = error;
      });
    } else {
      this.bookingParam.cronExpression = ((this.form.get('from').value == this.form.get('to').value) || !this.form.get('to').value)  ? null : "0 0 0 ? * " + this.form.get('cron').value + ' *';
      this.bookingParam.duration = this.form.get('dur').value;
      this.bookingParam.startTime = this.form.get('tt').value;
      this.bookingParam.meetingRoomId = this.form.get('room').value;
      this.bookingParam.from = this.format(this.form.get('from').value);
      this.bookingParam.to = this.format(this.form.get('to').value  ? this.form.get('to').value : this.form.get('from').value);
      this.bookingParam.description = this.form.get('desc').value;
      this.bookingParam.color = this.form.get('color').value;

      this.serverProtocolService.modifyBooingParam(this.bookingParam).subscribe( data => {
        this.openSuccess('Success to modify');

      }, error => {
        console.log(error);
        this.errorIsShown = true;
        this.errorMsg = error;
      });
    }

  }
  delete() {
    this.serverProtocolService.removeBookingInfo(this.data.requestId).subscribe(()=> {
      this.openSuccess('Success to delete.');
    });
  }
  openSuccess(msg: string) {
    this.logger.debug(msg);
    this.dialogRef.close();
    let snackBarRef = this.snackBar.open(msg, 'Close', { duration: 2000});
    snackBarRef.onAction().subscribe();
    snackBarRef.afterDismissed().subscribe( () => {this.logger.debug("dismissed.");});
  }
  openFailure(msg: string) {
    this.logger.debug("Fail to Save.");
    let snackBarRef = this.snackBar.open(msg, 'Close', { duration: 2000, panelClass: 'snack-popup'});
    snackBarRef.onAction().subscribe( () => this.dialogRef.close());
    snackBarRef.afterDismissed().subscribe( () => this.logger.debug("dismissed."));
  }
  cancel() {
    this.dialogRef.close('cancel');
  }
  format(date: Date) : string {
    //this.logger.debug(date);
    console.log(date);
    return [date.getFullYear()
      , ('0' + ( date.getMonth() + 1)).slice(-2)
      , ('0' + ( date.getDate())).slice(-2)].join('');
  }
  onColorPicker(event: any) {
      console.log(event);
  }
}



