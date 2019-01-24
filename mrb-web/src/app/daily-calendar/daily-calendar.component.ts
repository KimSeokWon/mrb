import {Component, Inject, OnInit} from '@angular/core';
import {ServerProtocolService} from "../service/server-protocol.service";
import {MeetingRoom} from "../model/meeting-room";
import {Observable} from "rxjs";

@Component({
  selector: 'app-daily-calendar',
  templateUrl: './daily-calendar.component.html',
  styleUrls: ['./daily-calendar.component.scss']
})
export class DailyCalendarComponent implements OnInit {
  meetingRooms$: Observable<MeetingRoom>;
  startDate = new Date();
  data: any = [
    { name: 'test', num: 1},
    { name: 'test', num: 2},
    { name: 'test', num: 3},
    { name: 'test', num: 4},
    { name: 'test', num: 5},
    { name: 'test', num: 6},
    { name: 'test', num: 7},
    { name: 'test', num: 8},
    { name: 'test', num: 9},
    { name: 'test', num: 10},
  ]
  constructor(
    @Inject(ServerProtocolService) private serverProtocol
  ) { }

  ngOnInit() {
    this.meetingRooms$ = this.serverProtocol.getMeetingRooms();
  }

}
