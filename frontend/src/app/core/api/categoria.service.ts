import { httpResource } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Categoria } from '../../modelos/categoria';

@Injectable({ providedIn: 'root' })
export class CategoriaService {
  readonly categoriasEmArvore = httpResource<Categoria[]>(() => `${environment.apiUrl}/categorias`, { defaultValue: [] });
}
