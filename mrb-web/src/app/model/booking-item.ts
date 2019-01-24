
export class BookingItem {
  constructor(
    public bookingId: number,
    public date: Date,
    public startTime: number,
    public endTime: number,
    public desc: string,
    public requestId: number,
    public roomId: number,
    public cnt: number,
  ){}
}
