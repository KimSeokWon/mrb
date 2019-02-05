import {Component, EventEmitter, Inject, Input, OnInit} from '@angular/core';
import {forkJoin, Observable} from "rxjs";
import {ServerProtocolService} from "../service/server-protocol.service";
import {MeetingRoom} from "../model/meeting-room";
import {NGXLogger} from "ngx-logger";
import {FormControl} from "@angular/forms";
import {TimeTable} from "../model/time-table";
import {BookingItem} from "../model/booking-item";
import {map} from "rxjs/operators";
import {BookDialogComponent} from "../book-dialog/book-dialog.component";
import {MatDialog} from "@angular/material";
import {ToolbarService} from '../service/toolbar-service';

@Component({
  selector: 'app-cal-table',
  templateUrl: './cal-table.component.html',
  styleUrls: ['./cal-table.component.scss']
})
export class CalTableComponent implements OnInit {
  startDate = new FormControl(new Date());
  private meetingRoom: MeetingRoom[];
  private timeTable: TimeTable[];
  dataSource: BookingItem[][];
  loadingFinish = false;
  constructor(
    public dialog: MatDialog,
    @Inject(ServerProtocolService) private serverProtocolService,
    @Inject(ToolbarService) private toolbarService,
    private logger: NGXLogger
  ){
    this.toolbarService.disableRegister = false;
    this.toolbarService.reloadAsObservable().subscribe( res => {
        if ( res ) {
            this.loadData();
        }
    });
  }
  ngOnInit() {

    let meetingRoom = this.serverProtocolService.getMeetingRooms();
    let timeTable = this.serverProtocolService.getTimeTable();

    forkJoin([meetingRoom, timeTable]).subscribe( data => {
      this.meetingRoom = data[0];
      this.timeTable = data[1];
      this.resetData();
      this.loadingFinish = true;
    });

  }
  loadData(date: string = null) {
    if ( date == null ) {
      this.logger.debug(this.startDate.value);
      date = this.format(this.startDate.value);
    }
    this.serverProtocolService.getBookingList(date).subscribe( data => {
      this.resetData();
      data.forEach( (item: BookingItem) => {
        for ( let i = item.startTime; i < item.endTime; i++ ) {
          this.dataSource[i][item.roomId] = item;
        }
      });
    });
  }
  resetData() {
    this.dataSource = new Array(this.timeTable.length);
    this.timeTable.forEach( t => {
      this.dataSource[t.time] = new Array(this.meetingRoom.length);
      this.meetingRoom.forEach( m => {
        this.dataSource[t.time][m.meetingRoomId] = null;
      })
    });
  }
  addEvent(action: string, event: any ) {
    this.logger.debug(action+ ", "+ event.value);
    if ( action == 'input') {
      let today: Date = (event.value as Date);
      this.loadData(this.format(today));
    }
  }

  getBookingData(date: string) {
    this.serverProtocolService.getBookingList(date)
  }

  format(date: Date) : string {
    //this.logger.debug(date);
    //console.log(date);
    return [date.getFullYear()
      , ('0' + ( date.getMonth() + 1)).slice(-2)
      , ('0' + ( date.getDate())).slice(-2)].join('');
  }
  openModifyWindow(obj: BookingItem) {
    if ( obj != null ) {
      this.logger.debug(obj.requestId);
      this.serverProtocolService.getBookingParam(obj.requestId).subscribe( res => {
        const dialogRef = this.dialog.open(BookDialogComponent, {
          width: '500px',
          data : res,
          panelClass: 'popup-card'
        });
        dialogRef.afterClosed().subscribe( data => {
          this.loadData(this.format(this.startDate.value));
        })
      });
    }
  }
  goBack() {
    //this.logger.debug(new Date(this.startDate.value.getTime()-24*60*1000*60));
    this.startDate.setValue(new Date(this.startDate.value.getTime()-24*60*1000*60));
    this.loadData(this.format(this.startDate.value));
  }
  goForward() {
    //this.logger.debug(new Date(this.startDate.value.getTime()+24*60*1000*60));
    this.startDate.setValue(new Date(this.startDate.value.getTime()+24*60*1000*60));
    this.loadData(this.format(this.startDate.value));
  }
}
