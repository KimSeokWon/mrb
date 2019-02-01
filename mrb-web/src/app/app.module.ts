import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {LoggerModule, NgxLoggerLevel} from "ngx-logger";
import {MaterialModule} from "./material.module";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import {ServerProtocolService} from "./service/server-protocol.service";
import {CalTableComponent} from "./cal-table/cal-table.component";
import {BookDialogComponent} from "./book-dialog/book-dialog.component";
import {ColorPickerModule} from "ngx-color-picker";
import {InitComponent} from "./init/init.component";

@NgModule({
  declarations: [
    AppComponent,
      InitComponent,
    CalTableComponent,
    BookDialogComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule,
    MaterialModule,
    LoggerModule.forRoot({level: NgxLoggerLevel.DEBUG}),
    ReactiveFormsModule,
    ColorPickerModule,
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
