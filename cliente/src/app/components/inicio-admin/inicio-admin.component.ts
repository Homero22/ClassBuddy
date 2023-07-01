import { Component } from '@angular/core';
import { Chart } from 'chart.js'; 
import { takeUntil, Subject } from 'rxjs';
import { InicioAdminService } from 'src/app/core/services/inicio-admin.service';

@Component({
  selector: 'app-inicio-admin',
  templateUrl: './inicio-admin.component.html',
  styleUrls: ['./inicio-admin.component.css']
})
export class InicioAdminComponent {

  private destroy$ = new Subject<any>();

  constructor(
    public srvInicioAdmin: InicioAdminService
  ){}

  ngOnInit(): void {
    //Called after the constructor, initializing input properties, and the first call to ngOnChanges.
    //Add 'implements OnInit' to the class.
    // this.getNumCard()
  }

  getNumCard(){
    this.srvInicioAdmin.getUsersTotal()
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: (res)=>{
        console.log("cantidad de usuarios ",res)
        this.srvInicioAdmin.usuariosT = res.body
      }
    })

    this.srvInicioAdmin.getMateriasTotales()
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: (value) =>{
          console.log("cantidad de materias ",value)
          this.srvInicioAdmin.materiasT = value.body
        },
    })

    this.srvInicioAdmin.getAdminTotales()
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: (value) =>{
          console.log("cantidad de administradores ",value)
          this.srvInicioAdmin.adminsT = value.body
      },
    })
  }
}
