import {Component, Inject, ViewChild} from '@angular/core';
import {MatDialog} from '@angular/material';
import {BookDialogComponent} from './book-dialog/book-dialog.component';
import {NGXLogger} from 'ngx-logger';
import {CalTableComponent} from './cal-table/cal-table.component';
import {ServerProtocolService} from './service/server-protocol.service';
import {ActivatedRoute, ActivatedRouteSnapshot, UrlSegment} from '@angular/router';
import {ToolbarService} from './service/toolbar-service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'mrb-web';
  addButtonTitle = 'book a room';
  showAddButon = true;

  @ViewChild(CalTableComponent) private tableComponent: CalTableComponent;

  constructor(
    public dialog: MatDialog,
    @Inject(ToolbarService) private toolbarService,
    public logger: NGXLogger,
  ) {
    this.toolbarService.disableRegisterAsObservable().subscribe( u => {
      this.logger.debug('toolbar flag :' + u);
      this.showAddButon = !u;
    });
  }

  openBookPopup(): void {
    const dialogRef = this.dialog.open(BookDialogComponent, {
      width: '500px',
      data : null,
      panelClass: 'popup-card'
    });
    dialogRef.afterClosed().subscribe( result => {
      if ( result !== 'cancel' ) {
        this.toolbarService.reload = true;
      }
    });
  }
}
