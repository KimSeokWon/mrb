import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {CalTableComponent} from "./cal-table/cal-table.component";
import {InitComponent} from "./init/init.component";

const routes: Routes = [
  {path: 'main', component: CalTableComponent},
  {path: '**', component: InitComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
