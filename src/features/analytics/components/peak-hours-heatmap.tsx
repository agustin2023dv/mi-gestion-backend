import { BarChart3 } from 'lucide-react';

// TODO: Accept heatmap data via props or fetch from analytics API
interface PeakHoursHeatmapProps {
  data?: { day: string; hours: number[] }[];
}

export function PeakHoursHeatmap({ data = [] }: PeakHoursHeatmapProps) {
  if (data.length === 0) {
    return (
      <div className="bg-white p-6 rounded-2xl border border-stone-200 shadow-sm">
        <h3 className="font-serif text-lg font-bold text-stone-900 mb-6">Mapa de Calor (Ventas por Hora)</h3>
        <div className="flex flex-col items-center justify-center py-16 text-center">
          <div className="w-14 h-14 bg-stone-50 rounded-full flex items-center justify-center mb-4">
            <BarChart3 className="w-7 h-7 text-stone-200" />
          </div>
          <h4 className="text-sm font-bold text-stone-900 mb-1">No hay datos de actividad</h4>
          <p className="text-xs text-stone-400">Los datos del mapa de calor aparecerán cuando haya ventas registradas.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white p-6 rounded-2xl border border-stone-200 shadow-sm overflow-x-auto">
      <h3 className="font-serif text-lg font-bold text-stone-900 mb-6">Mapa de Calor (Ventas por Hora)</h3>
      <div className="min-w-[600px]">
        {/* X-axis labels */}
        <div className="flex ml-10 mb-2">
          {Array.from({ length: 24 }).map((_, i) => (
            <div key={i} className="flex-1 text-center text-[10px] text-stone-400 font-mono">
              {i % 2 === 0 ? `${i}h` : ''}
            </div>
          ))}
        </div>
        
        {/* Grid */}
        <div className="flex flex-col gap-1">
          {data.map((row) => (
            <div key={row.day} className="flex items-center">
              <span className="w-10 text-xs font-medium text-stone-500">{row.day}</span>
              <div className="flex flex-1 gap-1">
                {row.hours.map((score, colIdx) => (
                  <div 
                    key={colIdx} 
                    className="flex-1 aspect-[2/3] sm:aspect-square rounded-[3px] transition-all hover:scale-125 cursor-pointer relative z-0 hover:z-10"
                    style={{ 
                      backgroundColor: score === 0 ? '#f5f5f4' : 'var(--color-primary, #1c1917)',
                      opacity: score === 0 ? 1 : (score * 0.25)
                    }}
                    title={`${row.day} a las ${colIdx}h - Nivel de actividad: ${score}`}
                  />
                ))}
              </div>
            </div>
          ))}
        </div>
        
        {/* Legend */}
        <div className="flex items-center justify-end gap-2 mt-6 text-xs text-stone-500">
          <span>Menos tráfico</span>
          <div className="flex gap-1">
            {[0, 1, 2, 3, 4].map(score => (
              <div 
                key={score} 
                className="w-4 h-4 rounded-[3px]"
                style={{
                  backgroundColor: score === 0 ? '#f5f5f4' : 'var(--color-primary, #1c1917)',
                  opacity: score === 0 ? 1 : (score * 0.25)
                }}
              />
            ))}
          </div>
          <span>Más tráfico</span>
        </div>
      </div>
    </div>
  );
}
