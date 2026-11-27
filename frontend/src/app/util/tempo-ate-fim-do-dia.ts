export function formataTempoAteFimDoDia(agora: Date): string {
  const fimDoDia = new Date(agora);
  fimDoDia.setHours(23, 59, 59, 999);
  const segundosRestantes = Math.max(0, Math.floor((fimDoDia.getTime() - agora.getTime()) / 1000));
  const horas = Math.floor(segundosRestantes / 3600);
  const minutos = Math.floor((segundosRestantes % 3600) / 60);
  const segundos = segundosRestantes % 60;
  return [horas, minutos, segundos].map((parte) => String(parte).padStart(2, '0')).join(':');
}
