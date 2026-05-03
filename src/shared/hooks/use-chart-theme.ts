import { useDesign } from "../contexts/design-context";


export const useChartTheme = () => {
  const { settings } = useDesign();

  return {
    primary: settings.primaryColor,
    grid: '#e7e5e4',
    text: '#78716c',
    tooltip: {
      contentStyle: {
        borderRadius: '12px',
        border: 'none',
        boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)',
        fontSize: '12px',
        fontWeight: 'bold',
        fontFamily: 'inherit'
      },
      itemStyle: {
        color: settings.primaryColor,
        fontWeight: 'bold'
      },
    },
    xAxis: {
      axisLine: false,
      tickLine: false,
      tick: { fill: '#78716c', fontSize: 12 },
      dy: 10
    },
    yAxis: {
      axisLine: false,
      tickLine: false,
      tick: { fill: '#78716c', fontSize: 12 }
    }
  };
};
