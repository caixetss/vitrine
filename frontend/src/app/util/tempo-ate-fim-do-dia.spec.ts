import { formataTempoAteFimDoDia } from './tempo-ate-fim-do-dia';

describe('formataTempoAteFimDoDia', () => {
  it('deve contar o tempo que falta até 23:59:59', () => {
    expect(formataTempoAteFimDoDia(new Date(2026, 10, 27, 18, 17, 50))).toBe('05:42:09');
  });

  it('deve mostrar zero no último segundo do dia', () => {
    expect(formataTempoAteFimDoDia(new Date(2026, 10, 27, 23, 59, 59))).toBe('00:00:00');
  });

  it('deve contar o dia inteiro à meia-noite', () => {
    expect(formataTempoAteFimDoDia(new Date(2026, 10, 27, 0, 0, 0))).toBe('23:59:59');
  });
});
