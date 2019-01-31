import {Injectable} from "@angular/core";

export class BookingParam {
  constructor (
    public from: string,
    public to: string,
    public startTime: number,
    public duration: number,
    public description: string,
    public cronExpression: string,
    public meetingRoomId: number,
    public requestId? : number,
    public fromDate?: Date,
    public toDate?: Date,
    public color?: string,
  ) {}

}
