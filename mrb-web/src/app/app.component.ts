import {Component, ViewChild} from '@angular/core';
import {MatDialog} from "@angular/material";
import {BookDialogComponent} from "./book-dialog/book-dialog.component";
import {NGXLogger} from "ngx-logger";
import {CalTableComponent} from "./cal-table/cal-table.component";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'mrb-web';
  addButtonTitle = 'book a room';

  @ViewChild(CalTableComponent) private tableComponent: CalTableComponent;

  constructor(
    public dialog: MatDialog,
    public logger: NGXLogger
  ) {

  }

  openBookPopup(): void {
    const dialogRef = this.dialog.open(BookDialogComponent, {
      width: '500px',
      data : null,
      panelClass: 'popup-card'
    });
    dialogRef.afterClosed().subscribe( result => {
      if ( result !== 'cancel' ) {
        this.tableComponent.loadData();
      }
    })
  }
}
