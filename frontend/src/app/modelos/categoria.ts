export interface Categoria {
  id: string;
  nome: string;
  slug: string;
  icone: string | null;
  subcategorias: Categoria[];
}
