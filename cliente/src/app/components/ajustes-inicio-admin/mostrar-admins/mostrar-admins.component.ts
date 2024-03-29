import { Component, ViewChild, AfterViewInit } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { takeUntil, Subject } from 'rxjs';
import { AdministradorService } from 'src/app/core/services/administrador.service';
import { UsuarioModel } from 'src/app/core/models/usuario';
import Swal from 'sweetalert2';


@Component({
  selector: 'app-mostrar-admins',
  templateUrl: './mostrar-admins.component.html',
  styleUrls: ['./mostrar-admins.component.css'],
})
export class MostrarAdminsComponent implements AfterViewInit {
  displayedColumns: string[] = ['index', 'nombre', 'estado', 'telefono', 'actions'];
  dataSource = new MatTableDataSource<UsuarioModel>();
  // tableIndex = 1;
  TypeView: number = 0;
  idAdmin!: number;
  idUser!: any;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  private destroy$ = new Subject<any>();

  constructor(public srvAdmins: AdministradorService) {
    // this.dataSource = new MatTableDataSource<UsuarioModel>();
   }

  ngOnInit(): void {
    // this.getAdministradores();
    // setTimeout(() => {
    //   this.dataSource = new MatTableDataSource<UsuarioModel>(this.srvAdmins.administradores);
    //   console.log("administradores this.dataSource ", this.dataSource);
    // }, 1000);
    // // this.dataSource = new MatTableDataSource<UsuarioModel>(this.srvAdmins.administradores);
    // // console.log("administradores this.dataSource ", this.dataSource);
    this.idUser = sessionStorage.getItem('id');
  }

  ngAfterViewInit() {
    this.getAdministradores();

    // this.dataSource.paginator = this.paginator;
  }

  view(op: number, id: number) {
    if (op == 2) {
      this.idAdmin = id;
      // this.getAdmin(id);
    }
    this.TypeView = op;
    console.log("idAdmin en mostrar", this.idAdmin);
  }

  regresar() {
    this.getAdministradores();
    this.TypeView = 0;


  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLocaleLowerCase();
  }

  getAdministradores() {
    this.srvAdmins
      .getAdministradores()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (value) => {
          this.srvAdmins.administradores = value.body;
          this.dataSource = new MatTableDataSource<UsuarioModel>(this.srvAdmins.administradores);
          console.log("administradores ", this.srvAdmins.administradores);
        },
        complete: () => {
          this.dataSource.paginator = this.paginator;

        },
      });
  }

  getRowIndex(index: number): number {
    return index + 1;
  }

  eliminarAdmin(id: number) {
    if( id == this.idUser ){
      Swal.fire('No puede eliminar su propio usuario', '', 'error')
    }else{
    Swal.fire({
      title: '¿Está seguro de eliminar este administrador?',
      icon: 'warning',
      showDenyButton: true,
      showCancelButton: true,
      confirmButtonText: 'Cambiar rol',
      denyButtonText: `Cambiar estado`,
    }).then((result) => {
      
      if (result.isConfirmed ) {
        this.CambiarRol(id)
      } else if (result.isDenied ) {
        this.CambiarEstado(id)
      }
    
    })
  }
  }

  CambiarRol(id: number) {

    this.srvAdmins.getAdministrador(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (value) => {
          if (value.status){
            let admin = value.body;
            admin.rol = 'USUARIO';
            this.srvAdmins.putAdministrador(id, admin)
              .pipe(takeUntil(this.destroy$))
              .subscribe({
                next: (value) => {
                  console.log("value ", value);
                  this.getAdministradores();
                },
                complete: () => {
                  Swal.fire('Rol cambiado', '', 'success')
                }
              })
          }
        }
      })
  }

  

  CambiarEstado(id: number) {
    this.srvAdmins.deleteAdministrador(id)
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next: (value) => {
        console.log("value ", value);
        this.getAdministradores();
        Swal.fire(value.message,'', 'success')

      },
      complete: () => {
      }
    })
  }

  ngOnDestroy(): void {
    this.destroy$.next({});
    this.destroy$.complete();
  }
}
