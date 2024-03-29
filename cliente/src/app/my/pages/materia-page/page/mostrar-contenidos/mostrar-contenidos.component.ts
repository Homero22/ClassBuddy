import { Component, OnInit } from '@angular/core';
import { Subject, takeUntil, timeInterval } from 'rxjs';
import { ApunteService } from 'src/app/core/services/apunte.service';
import { ContenidoService } from 'src/app/core/services/contenido.service';
import { UsuarioService } from 'src/app/core/services/usuario.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-mostrar-contenidos',
  templateUrl: './mostrar-contenidos.component.html',
  styleUrls: ['./mostrar-contenidos.component.css']
})
export class MostrarContenidosComponent implements OnInit{

  private destroy$ = new Subject<any>();

  viewApunte!: number;
  viewContenido!: number;
  idUser!: any;
  isData!: boolean;
  idApunte!: number;

  constructor(
    public srvApunte: ApunteService,
    public srvContenido: ContenidoService,
    public srvUsuario: UsuarioService
  ) { }

  ngOnInit(): void {
    this.idUser = sessionStorage.getItem("id");

    this.srvApunte.selectIdApunte$
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next:(_idApunte)=>{
        this.idApunte = _idApunte;
        console.log("Valor de idApunte =>",this.idApunte);
      }
    })

    this.srvContenido.selectViewContenido$
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next:(_viewContenido)=>{
        this.viewContenido = _viewContenido;
        console.log("Valor de viewContenido =>",this.viewContenido);
      }
    })


    console.log("Valor de idUser =>",this.idUser);
    this.isData = false;
    this.getContenidos();
  }

  //Funcion para regresar a la vista de apuntes
  returnListApunte(){
    this.viewApunte = 1;
    this.srvApunte.setApunteView(this.viewApunte);
  }

  //Funcion para mostrar los contenidos guardados
  getContenidos(){
    const idUser = this.idUser;

    Swal.fire({
      title: 'Cargando Contenidos',
      text: 'Espere un momento por favor...',
      icon: 'info',
      allowOutsideClick: false,
      showConfirmButton: false,
      didOpen: ()=>{
        Swal.showLoading();
      }
    })

    this.srvContenido.getContenidosGuardados(this.idApunte)
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: (resContenidos)=>{
        if(resContenidos.status){
          Swal.close();
          console.log("Valor de resContenidos =>",resContenidos);
          this.srvContenido.contentSimilarData = resContenidos.body;
          this.isData = true;
        }else{
          Swal.close();
        }
      },
      error: (err)=>{
        Swal.fire({
          title: 'Error al cargar los contenidos',
          text: 'Por favor intente de nuevo',
          icon: 'error',
          allowOutsideClick: false,
          showConfirmButton: true,
          confirmButtonText: 'Aceptar'
        });
      },
      complete: ()=>{
        console.log("Peticion completa");
      }
    });
  }

  //Funcion para mostrar el contenido
  showContenido(idApunteContenido: number, contenidoTitulo: string){
    this.viewContenido = 1;
    this.srvContenido.setTitle(contenidoTitulo);
    this.srvContenido.setIdContenido(idApunteContenido);
  }

  deleteContenido(idContent: number){

    console.log("Valor de idContent =>",idContent);

    Swal.fire({
      title: '¿Está seguro de eliminar el contenido?',
      text: 'Esta acción no se puede revertir',
      icon: 'warning',
      allowOutsideClick: false,
      showConfirmButton: true,
      showCancelButton: true,
      confirmButtonText: 'Aceptar',
      cancelButtonText: 'Cancelar'
    }).then((result)=>{
      if(result.isConfirmed){
        this.srvContenido.deleteContenidoGuardado(idContent)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (resDelete)=>{
            if(resDelete.status){
              Swal.fire({
                title: 'Contenido eliminado',
                text: 'El contenido se ha eliminado correctamente',
                icon: 'success',
                allowOutsideClick: false,
                showConfirmButton: false,
                timer: 2500
              })

            }else{
              Swal.fire({
                title: 'Error al eliminar el contenido',
                text: 'Por favor intente de nuevo',
                icon: 'error',
                allowOutsideClick: false,
                showConfirmButton: true,
                confirmButtonText: 'Aceptar'
              });
            }
            setTimeout(() => {
              this.getContenidos();
            }
            , 2500);
          },
          error: (err)=>{
            Swal.fire({
              title: 'Error al eliminar el contenido :(',
              text: 'Por favor intente de nuevo',
              icon: 'error',
              allowOutsideClick: false,
              showConfirmButton: true,
              confirmButtonText: 'Aceptar'
            });
          },
          complete: ()=>{
            console.log("Peticion completa");
            this.getContenidos();
          }
        })
      }
    })
  }


  ngOnDestroy(): void {
    this.destroy$.next({});
    this.destroy$.complete();
  }
}
