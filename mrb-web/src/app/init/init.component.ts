import {Component, Inject, OnInit} from "@angular/core";
import {NGXLogger} from "ngx-logger";
import {ServerProtocolService} from "../service/server-protocol.service";
import {FormControl, Validators} from '@angular/forms';
import {ToolbarService} from '../service/toolbar-service';
import {map, retryWhen} from 'rxjs/operators';
import {interval, Observable} from 'rxjs';
import {Router} from '@angular/router';
import {extractDirectiveDef} from '@angular/core/src/render3/definition';

class TimeObject {
    constructor (
        public timeIndex: number,
        public timeString: string
    ) {}
}
@Component({
    selector: 'init',
    templateUrl: './init.component.html',
    styleUrls: ['./init.component.scss']
})
export class InitComponent implements OnInit{
    started = false;
    systemState = 0;
    timeObj: TimeObject[] = [];
    room_count: number[] = [20];
    startControl = new FormControl('', Validators.required);
    finishControl = new FormControl('', Validators.required);
    countControl = new FormControl('', Validators.required);

    constructor (
        @Inject(ServerProtocolService) private serverProtocolService,
        @Inject(ToolbarService) private toolbarService,
        private router: Router,
        private logger: NGXLogger
    ) {
        for ( let i = 0; i < 48; i++ ) {
            this.timeObj[i] = new TimeObject(
                i,
                '' + ( (i / 2 ) < 10 ? ('0' + ( Math.round(i / 2 - .5) )) : Math.round(i / 2 - .5) ) + ':' + (( i % 2 ) === 0 ? '00' : '30')
            );
        }
        for ( let i = 0 ; i < 20 ; i++ ) {
            this.room_count[i] = i + 1;
        }
        this.toolbarService.disableRegister = true;
    }
    ngOnInit(): void {
        this.serverProtocolService.getInitStatus().subscribe( data => {
            this.logger.debug('Init State: ' + data.status);
            if ( data.status === 9 ) {
                this.router.navigateByUrl('/main', {skipLocationChange: false});
            } else {
                this.serverProtocolService.resetStorage();
                this.systemState = data;
                this.started = true;
            }
        });
    }

    onSendInitValues() {
        this.serverProtocolService.sendInitParam({
            startHour: this.startControl.value,
            endHour: this.finishControl.value,
            count: this.countControl.value
        }).subscribe(data => {
            this.logger.debug('In progress for initialization....');
            if ( data === 9 ) {
                this.router.navigateByUrl('/main', {skipLocationChange: false});
            } else {
                this.getState();
            }
        });
    }

    getState(): void {
        this.logger.debug(`getState() is called....`);
        this.serverProtocolService.getInitStatus().pipe(
            map( res => {
                this.logger.debug(`return value: ${res}`);
                if ( res !== 9 ) {
                    setInterval( () => {
                        this.getState();
                    }, 1000);
                } else {
                    this.router.navigateByUrl('/main', {skipLocationChange: false});
                }
            }));
    }
}
