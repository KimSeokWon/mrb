import {EventEmitter, Injectable} from '@angular/core';
import {Observable} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ToolbarService {
    private _disableRegister: EventEmitter<boolean> = new EventEmitter<boolean>();

    public set disableRegister(flag: boolean) {
        this._disableRegister.emit(flag);
    }

    public disableRegisterAsObservable(): Observable<boolean> {
        return this._disableRegister.asObservable();
    }
}
