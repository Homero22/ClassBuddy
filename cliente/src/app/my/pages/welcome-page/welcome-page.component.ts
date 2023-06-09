import { Component } from '@angular/core';
import { TareaService } from 'src/app/core/services/tarea.service';

@Component({
  selector: 'app-welcome-page',
  templateUrl: './welcome-page.component.html',
  styleUrls: [ './welcome-page.component.css'
  ]
})
export class WelcomePageComponent {

  constructor(
    private tareaService: TareaService
  ) { /*this.permisos() */}
  permiso!: boolean 
  // permiso: boolean = false

  ngOnInit(): void {
    this.permisos();
  }

  permisos(){
    const rol =  sessionStorage.getItem('rol');
    console.log("rol => ", rol)
    if(rol == 'USUARIO'){
      this.permiso = false;
      console.log ("tiene permiso ? ",this.permiso)
    }
    if(rol == 'ADMINISTRADOR'){
      this.permiso = true;
      console.log ("tiene permiso ? ",this.permiso)
    }
    // this.rol = parseInt(this.idUser);
  }
  // ------------------------ Recordatorios ------------------------
/*
  recordatorios: any = {
    message: "desde el front",
    phone: "593999778684"
};

  getRecordatorios(){
    this.tareaService.getRecordatorios()
      .subscribe(
        (res: any) => {
          this.recordatorios = res;
          console.log("recordatorios =>", this.recordatorios);
        },
        err => console.log(err)
      );
  }

  postRecordatorio(){
    this.tareaService.postRecordatorio(this.recordatorios)
      .subscribe(
        (res: any) => {
          console.log("res =>", res);
        },
        err => console.log(err)
      );
  }
*/

  ngOnDestroy(): void {
    //Called once, before the instance is destroyed.
    //Add 'implements OnDestroy' to the class.
    
  }
}
