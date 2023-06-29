import { Component, OnInit } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import { promptModel  } from 'src/app/core/models/gpt';
import { ApunteService } from 'src/app/core/services/apunte.service';
import { GptService } from 'src/app/core/services/gpt.service';
import Swal from 'sweetalert2';

// Importamos el promptModel de servidork



@Component({
  selector: 'app-contenido-apunte',
  templateUrl: './contenido-apunte.component.html',
  styleUrls: ['./contenido-apunte.component.css']
})
export class ContenidoApunteComponent implements OnInit {

  // Variables
  private destroy$ = new Subject<void>();
  idUser: any;
  idApunte: any;

  apunteContent: any;
  mensaje!: string;
  promptResult: any;
  showMaterialContent: boolean = false;

  constructor(
    public srvApuntes: ApunteService,
    public srvGPT: GptService
  ) { }

  ngOnInit() {

    this.srvApuntes.selectIdApunte$
    .pipe(takeUntil(this.destroy$))
    .subscribe((idApunte: any) => {
      this.idApunte = idApunte;
    });

    console.log("Valor del idApunte =>",this.idApunte);

    this.idUser = localStorage.getItem('idUser');


    this.getApunteData();
    // this.getMaterialEstudio();
  }

  getApunteData(){

    Swal.fire({
      title: 'Cargando contenido del apunte',
      didOpen: () => {
        Swal.showLoading()
      },
    });

    this.srvApuntes.getApunteIndividual(this.idApunte)
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: (res: any) => {
        if(res.status){
          Swal.close();
          console.log("Respuesta del servidor =>",res);
          this.apunteContent = res.body;
          console.log("Contenido del apunte =>",this.apunteContent);
          this.getPrompt();
        }
      },
      error: (err: any) => {
        console.log("Error del servidor =>",err);
      },
      complete: () => {
        console.log("Petición completa");
      }
    });
  }

  getPrompt() {

    this.srvGPT.postPrompt(this.idApunte, this.apunteContent)
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: (res: any) => {
        console.log("Respuesta del servidor =>",res);
        this.mensaje = res.body;
      },
      error: (err: any) => {
        console.log("Error del servidor =>",err);
      },
      complete: () => {
        console.log("Petición completa");
      }
    });
  }


  generateContent(){

    const promptData: promptModel  = {
      mensaje: this.mensaje
    }

    console.log("Valor del promptMessage =>",this.mensaje);
    console.log("tipo de message => ", typeof this.mensaje);

    Swal.fire({
      title: 'Generando Material...',
      didOpen: () => {
        Swal.showLoading()
      },
    });


    this.srvGPT.postMessage(promptData)
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: (res: any) => {
        if(res.status){
          console.log("Respuesta del servidor =>",res);
          this.promptResult = res.info;
          this.showMaterialContent = true;
        }
        Swal.close();
      },
      error: (err: any) => {
        console.log("Error del servidor =>",err);
      },
      complete: () => {
        console.log("Petición completa");
      }
    });
  }




  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}