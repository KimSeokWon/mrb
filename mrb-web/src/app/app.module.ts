import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {LoggerModule, NgxLoggerLevel} from "ngx-logger";
import {MaterialModule} from "./material.module";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import {DailyCalendarComponent} from "./daily-calendar/daily-calendar.component";
import {ServerProtocolService} from "./service/server-protocol.service";
import {CalTableComponent} from "./cal-table/cal-table.component";
import {BookDialogComponent} from "./book-dialog/book-dialog.component";

@NgModule({
  declarations: [
    AppComponent,
    DailyCalendarComponent,
    CalTableComponent,
    BookDialogComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule,
    MaterialModule,
    LoggerModule.forRoot({level: NgxLoggerLevel.DEBUG}),
    ReactiveFormsModule,
  ],
  providers: [
    ServerProtocolService
  ],
  bootstrap: [AppComponent],
  entryComponents: [
    BookDialogComponent
  ]
})
export class AppModule { }
