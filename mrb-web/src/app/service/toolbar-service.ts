import {EventEmitter, Injectable} from '@angular/core';
import {Observable} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ToolbarService {
    private _disableRegister: EventEmitter<boolean> = new EventEmitter<boolean>();
    private _reload: EventEmitter<boolean> = new EventEmitter<boolean>();

    public set reload(flag: boolean) {
        this._reload.emit(flag);
    }
    public reloadAsObservable(): Observable<boolean> {
        return this._reload.asObservable();
    }
    public set disableRegister(flag: boolean) {
        this._disableRegister.emit(flag);
    }

    public disableRegisterAsObservable(): Observable<boolean> {
        return this._disableRegister.asObservable();
    }
}
