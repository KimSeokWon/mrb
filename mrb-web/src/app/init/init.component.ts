import {Component, Inject, OnInit} from "@angular/core";
import {NGXLogger} from "ngx-logger";
import {ServerProtocolService} from "../service/server-protocol.service";
import {FormControl} from "@angular/forms";

class TimeObject {
    constructor (
        timeIndex: number,
        timeString: string
    ) {}
}
@Component({
    selector: 'init',
    templateUrl: './init.component.html',
    styleUrls: ['./init.component.scss']
})
export class InitComponent implements OnInit{
    started = false;
    systemState: number = 0;
    timeObj: TimeObject[] = [];
    room_count: number[] = [20];
    startControl = new FormControl();
    finishControl = new FormControl();
    countControl = new FormControl();

    constructor (
        @Inject(ServerProtocolService) private serverProtocolService,
        private logger: NGXLogger
    ) {
        for ( var i = 0; i< 48; i++ ) {
            this.timeObj[i] = new TimeObject(
                i,
                '' + ( (i / 2 ) < 10 ? '0' + (i / 2 ) : i /2 ) + ':' + (i % 2 == 0 ? '00' : '30')
            );
        }
        for ( var i = 0 ;i < 20 ;i++ ) {
            this.room_count[i] = i+1;
        }
    }
    ngOnInit(): void {
        this.serverProtocolService.getInitStatus().subscribe( data => {
            this.logger.debug("Init State: " + data);
            this.systemState = data;
            this.started = true;
        });
    }

    onSendInitValues() {
        this.serverProtocolService.sendInitParam( {
           startHour: this.startControl.value,
           endHour: this.finishControl.value,
           count: this.countControl.value
        }) .subscribe( data => {
                this.logger.debug('In progress for initialization....');
            }
        );
    }
}