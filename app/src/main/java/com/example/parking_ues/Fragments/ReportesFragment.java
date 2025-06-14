package com.example.parking_ues.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parking_ues.Adapters.GestionarUsuariosAdapter;
import com.example.parking_ues.Adapters.HistoriaTransaccionesAdapter;
import com.example.parking_ues.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class ReportesFragment extends Fragment {

    private PieChart graficoCircular;
    private BarChart graficoBarrasComercial, graficoBarrasVIP;
    private TextView lblTotalIngresos, lblTotalGastosOperativos, lblError;
    private Button btnReporteFinanciero, btnHistorialExcel;
    private RecyclerView rvcHistorialTransacciones;

    public static ReportesFragment newInstance(String param1, String param2) {
        ReportesFragment fragment = new ReportesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.fragment_reportes, container, false);

        asociarElementosXML(view);
        configurarGraficoCircular();
        configurarGraficoBarComerciales();
        configurarGraficoBarVIP();

        rvcHistorialTransacciones.setLayoutManager(new LinearLayoutManager(getContext()));
        HistoriaTransaccionesAdapter adapter = new HistoriaTransaccionesAdapter(getContext(), getParentFragmentManager());
        rvcHistorialTransacciones.setAdapter(adapter);

        return view;
    }

    public void asociarElementosXML(View view){
        graficoCircular = view.findViewById(R.id.graficoCircular);
        graficoBarrasComercial = view.findViewById(R.id.graficoBarrasComercial);
        graficoBarrasVIP = view.findViewById(R.id.graficoBarrasVIP);
        lblTotalIngresos = view.findViewById(R.id.lblTotalIngresos);
        lblTotalGastosOperativos = view.findViewById(R.id.lblTotalGastosOperativos);
        lblError = view.findViewById(R.id.lblError);
        btnReporteFinanciero = view.findViewById(R.id.btnReporteFinanciero);
        btnHistorialExcel = view.findViewById(R.id.btnHistorialExcel);
        rvcHistorialTransacciones = view.findViewById(R.id.rvcHistorialTransacciones);
    }

    private void configurarGraficoCircular() {
        try {
            // Datos de ejemplo: autos estacionados por hora (7AM - 7PM)
            int[] autosPorHora = {10, 5, 5, 6, 7, 9, 9, 8, 7, 5, 4, 2, 1};

            List<PieEntry> horas = new ArrayList<>();

            // Generar etiquetas de horas (7AM a 7PM)
            String[] etiquetasHoras = new String[]{
                    "7AM", "8AM", "9AM", "10AM", "11AM", "12PM",
                    "1PM", "2PM", "3PM", "4PM", "5PM", "6PM", "7PM"
            };

            // Agregar datos al gráfico
            for(int i = 0; i < autosPorHora.length; i++) {
                horas.add(new PieEntry(autosPorHora[i], etiquetasHoras[i]));
            }

            PieDataSet conjuntoDatos = new PieDataSet(horas, "");
            conjuntoDatos.setValueLinePart1OffsetPercentage(100f);
            conjuntoDatos.setValueLinePart1Length(0.5f);
            conjuntoDatos.setValueLinePart2Length(0.3f);
            conjuntoDatos.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

            // Configurar colores para las secciones (mismos colores)
            List<Integer> colores = new ArrayList<>();
            colores.add(Color.parseColor("#FF6B6B"));
            colores.add(Color.parseColor("#4ECDC4"));
            colores.add(Color.parseColor("#45B7D1"));
            colores.add(Color.parseColor("#FFBE0B"));
            colores.add(Color.parseColor("#FB5607"));
            colores.add(Color.parseColor("#FF006E"));
            colores.add(Color.parseColor("#8338EC"));
            colores.add(Color.parseColor("#3A86FF"));
            colores.add(Color.parseColor("#06D6A0"));
            colores.add(Color.parseColor("#118AB2"));
            colores.add(Color.parseColor("#073B4C"));
            colores.add(Color.parseColor("#EF476F"));
            colores.add(Color.parseColor("#FFD166"));

            conjuntoDatos.setColors(colores);
            conjuntoDatos.setYValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
            conjuntoDatos.setValueTextSize(8f); // Tamaño
            conjuntoDatos.setValueTextColor(Color.BLACK); // Cambiado a negro para mejor contraste
            conjuntoDatos.setSliceSpace(2f); // Espacio reducido
            conjuntoDatos.setSelectionShift(8f);

            PieData datos = new PieData(conjuntoDatos);
            datos.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf((int) value);
                }
            });

            graficoCircular.setData(datos);
            graficoCircular.setUsePercentValues(false);
            graficoCircular.getDescription().setEnabled(false);
            graficoCircular.setExtraOffsets(8, 8, 8, 8); // Offsets reducidos
            graficoCircular.setDragDecelerationFrictionCoef(0.95f);
            graficoCircular.setDrawHoleEnabled(true);
            graficoCircular.setHoleColor(Color.WHITE);
            graficoCircular.setHoleRadius(30f); // Agujero más pequeño = gráfico más grande
            graficoCircular.setTransparentCircleRadius(35f); // Círculo transparente más pequeño
            graficoCircular.setRotationEnabled(true);
            graficoCircular.setHighlightPerTapEnabled(true);
            graficoCircular.setDrawEntryLabels(false); // Ocultar etiquetas dentro del gráfico
            graficoCircular.setMinAngleForSlices(10f); // Ocultar etiquetas para porciones pequeñas

            // Configurar leyenda vertical a la derecha
            Legend leyenda = graficoCircular.getLegend();
            leyenda.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
            leyenda.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            leyenda.setOrientation(Legend.LegendOrientation.VERTICAL);
            leyenda.setDrawInside(false);
            leyenda.setXEntrySpace(5f);
            leyenda.setYEntrySpace(3f);
            leyenda.setTextSize(9f); // Tamaño reducido
            leyenda.setTextColor(Color.BLACK);
            leyenda.setWordWrapEnabled(true); // Ajuste de texto automático

            // Animación y refresco
            graficoCircular.animateY(1000);
            graficoCircular.invalidate();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al cargar gráficos: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //CONFIGURAR EL GRAFICO DE BARRAS PARA LOS ESTACIONAMIENTOS COMERCIALES
    private void configurarGraficoBarComerciales() {
        try {
            // Datos de ejemplo: ingresos por hora (7AM - 7PM)
            float[] ingresosPorHora = {150, 200, 18, 220, 30, 30, 40, 30, 35, 3, 25, 27, 71};

            // Etiquetas de horas
            String[] etiquetasHoras = {"7AM", "8AM", "9AM", "10AM", "11AM", "12PM", "1PM", "2PM", "3PM", "4PM", "5PM", "6PM", "7PM"};

            // Preparar entradas de datos
            List<BarEntry> entradas = new ArrayList<>();
            for (int i = 0; i < ingresosPorHora.length; i++) {
                entradas.add(new BarEntry(i, ingresosPorHora[i]));
            }

            // Crear conjunto de datos
            BarDataSet conjuntoDatos = new BarDataSet(entradas, "Ingresos por hora");

            // Configurar colores  esquema que el circular
            List<Integer> colores = new ArrayList<>();
            colores.add(Color.parseColor("#FF6B6B"));
            colores.add(Color.parseColor("#4ECDC4"));
            colores.add(Color.parseColor("#45B7D1"));
            colores.add(Color.parseColor("#FFBE0B"));
            colores.add(Color.parseColor("#FB5607"));
            colores.add(Color.parseColor("#FF006E"));
            colores.add(Color.parseColor("#8338EC"));
            colores.add(Color.parseColor("#3A86FF"));
            colores.add(Color.parseColor("#06D6A0"));
            colores.add(Color.parseColor("#118AB2"));
            colores.add(Color.parseColor("#073B4C"));
            colores.add(Color.parseColor("#EF476F"));
            colores.add(Color.parseColor("#FFD166"));

            //CONFIGURAR LOS INGRESOS
            conjuntoDatos.setColors(colores);
            conjuntoDatos.setValueTextSize(6f);
            conjuntoDatos.setValueTextColor(Color.BLACK);
            conjuntoDatos.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return "$" + (int) value;
                }
            });

            // Crear datos de barras
            BarData datosBarras = new BarData(conjuntoDatos);
            datosBarras.setBarWidth(0.7f); // Ancho de barras

            // Configurar el gráfico
            graficoBarrasComercial.setData(datosBarras);
            graficoBarrasComercial.getDescription().setEnabled(false);
            graficoBarrasComercial.setDrawValueAboveBar(true);
            graficoBarrasComercial.setFitBars(true); // Ajustar todas las barras en la vista
            graficoBarrasComercial.setExtraOffsets(10, 10, 10, 30); // left, top, right, bottom


            // Configurar eje X
            XAxis ejeX = graficoBarrasComercial.getXAxis();
            ejeX.setPosition(XAxis.XAxisPosition.BOTTOM);
            ejeX.setDrawGridLines(false);
            ejeX.setValueFormatter(new IndexAxisValueFormatter(etiquetasHoras));
            ejeX.setLabelCount(etiquetasHoras.length);
            ejeX.setGranularity(1f);
            ejeX.setTextSize(3f);

            // Configurar eje Y izquierdo
            YAxis ejeYIzquierdo = graficoBarrasComercial.getAxisLeft();
            ejeYIzquierdo.setAxisMinimum(0);
            ejeYIzquierdo.setDrawGridLines(true);
            ejeYIzquierdo.setGranularity(50f);
            ejeYIzquierdo.setTextSize(8f);
            ejeYIzquierdo.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return "$" + (int) value;
                }
            });

            // Deshabilitar eje Y derecho
            YAxis ejeYDerecho = graficoBarrasComercial.getAxisRight();
            ejeYDerecho.setEnabled(false);

            // CONFIGURAR LEYENDA VERTICAL - CORRECCIÓN PRINCIPAL
            Legend leyenda = graficoBarrasComercial.getLegend();
            leyenda.setEnabled(true); // HABILITAR la leyenda (era false antes)
            leyenda.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            leyenda.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            leyenda.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            leyenda.setDrawInside(false);
            leyenda.setXEntrySpace(13f);
            leyenda.setYEntrySpace(13f);
            leyenda.setTextSize(6f);
            leyenda.setTextColor(Color.BLACK);
            leyenda.setWordWrapEnabled(true);
            leyenda.setFormToTextSpace(2f); // Padding entre el color y el texto

            // CREAR ENTRADAS PERSONALIZADAS PARA LA LEYENDA
            List<LegendEntry> entradasLeyenda = new ArrayList<>();
            for (int i = 0; i < etiquetasHoras.length; i++) {
                LegendEntry entrada = new LegendEntry();
                entrada.label = etiquetasHoras[i] ;
                entrada.formColor = colores.get(i % colores.size());
                entrada.form = Legend.LegendForm.SQUARE;
                entrada.formSize = 8f;
                entradasLeyenda.add(entrada);
            }
            leyenda.setCustom(entradasLeyenda);

            // Configurar interactividad
            graficoBarrasComercial.setTouchEnabled(true);
            graficoBarrasComercial.setDragEnabled(true);
            graficoBarrasComercial.setScaleEnabled(true);
            graficoBarrasComercial.setPinchZoom(true);

            // Animación
            graficoBarrasComercial.animateY(1000);
            graficoBarrasComercial.invalidate();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error en gráfico de barras: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    //CONFIGURAR EL GRAFICO DE BARRAS PARA LOS ESTACIONAMIENTOS VIP
    private void configurarGraficoBarVIP() {
        try {
            // Datos de ejemplo: ingresos por hora (7AM - 7PM)
            float[] ingresosPorHora = {150, 200, 18, 220, 30, 30, 40, 30, 35, 3, 25, 27, 71};

            // Etiquetas de horas
            String[] etiquetasHoras = {"7AM", "8AM", "9AM", "10AM", "11AM", "12PM", "1PM", "2PM", "3PM", "4PM", "5PM", "6PM", "7PM"};

            // Preparar entradas de datos
            List<BarEntry> entradas = new ArrayList<>();
            for (int i = 0; i < ingresosPorHora.length; i++) {
                entradas.add(new BarEntry(i, ingresosPorHora[i]));
            }

            // Crear conjunto de datos
            BarDataSet conjuntoDatos = new BarDataSet(entradas, "Ingresos por hora");

            // Configurar colores  esquema que el circular
            List<Integer> colores = new ArrayList<>();
            colores.add(Color.parseColor("#FF6B6B"));
            colores.add(Color.parseColor("#4ECDC4"));
            colores.add(Color.parseColor("#45B7D1"));
            colores.add(Color.parseColor("#FFBE0B"));
            colores.add(Color.parseColor("#FB5607"));
            colores.add(Color.parseColor("#FF006E"));
            colores.add(Color.parseColor("#8338EC"));
            colores.add(Color.parseColor("#3A86FF"));
            colores.add(Color.parseColor("#06D6A0"));
            colores.add(Color.parseColor("#118AB2"));
            colores.add(Color.parseColor("#073B4C"));
            colores.add(Color.parseColor("#EF476F"));
            colores.add(Color.parseColor("#FFD166"));

            //CONFIGURAR LOS INGRESOS
            conjuntoDatos.setColors(colores);
            conjuntoDatos.setValueTextSize(6f);
            conjuntoDatos.setValueTextColor(Color.BLACK);
            conjuntoDatos.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return "$" + (int) value;
                }
            });

            // Crear datos de barras
            BarData datosBarras = new BarData(conjuntoDatos);
            datosBarras.setBarWidth(0.7f); // Ancho de barras

            // Configurar el gráfico
            graficoBarrasVIP.setData(datosBarras);
            graficoBarrasVIP.getDescription().setEnabled(false);
            graficoBarrasVIP.setDrawValueAboveBar(true);
            graficoBarrasVIP.setFitBars(true); // Ajustar todas las barras en la vista
            graficoBarrasVIP.setExtraOffsets(10, 10, 10, 30); // left, top, right, bottom


            // Configurar eje X
            XAxis ejeX = graficoBarrasVIP.getXAxis();
            ejeX.setPosition(XAxis.XAxisPosition.BOTTOM);
            ejeX.setDrawGridLines(false);
            ejeX.setValueFormatter(new IndexAxisValueFormatter(etiquetasHoras));
            ejeX.setLabelCount(etiquetasHoras.length);
            ejeX.setGranularity(1f);
            ejeX.setTextSize(3f);

            // Configurar eje Y izquierdo
            YAxis ejeYIzquierdo = graficoBarrasVIP.getAxisLeft();
            ejeYIzquierdo.setAxisMinimum(0);
            ejeYIzquierdo.setDrawGridLines(true);
            ejeYIzquierdo.setGranularity(50f);
            ejeYIzquierdo.setTextSize(8f);
            ejeYIzquierdo.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return "$" + (int) value;
                }
            });

            // Deshabilitar eje Y derecho
            YAxis ejeYDerecho = graficoBarrasVIP.getAxisRight();
            ejeYDerecho.setEnabled(false);

            // CONFIGURAR LEYENDA VERTICAL - CORRECCIÓN PRINCIPAL
            Legend leyenda = graficoBarrasVIP.getLegend();
            leyenda.setEnabled(true); // HABILITAR la leyenda (era false antes)
            leyenda.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            leyenda.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            leyenda.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            leyenda.setDrawInside(false);
            leyenda.setXEntrySpace(13f);
            leyenda.setYEntrySpace(13f);
            leyenda.setTextSize(6f);
            leyenda.setTextColor(Color.BLACK);
            leyenda.setWordWrapEnabled(true);
            leyenda.setFormToTextSpace(2f); // Padding entre el color y el texto

            // CREAR ENTRADAS PERSONALIZADAS PARA LA LEYENDA
            List<LegendEntry> entradasLeyenda = new ArrayList<>();
            for (int i = 0; i < etiquetasHoras.length; i++) {
                LegendEntry entrada = new LegendEntry();
                entrada.label = etiquetasHoras[i] ;
                entrada.formColor = colores.get(i % colores.size());
                entrada.form = Legend.LegendForm.SQUARE;
                entrada.formSize = 8f;
                entradasLeyenda.add(entrada);
            }
            leyenda.setCustom(entradasLeyenda);

            // Configurar interactividad
            graficoBarrasVIP.setTouchEnabled(true);
            graficoBarrasVIP.setDragEnabled(true);
            graficoBarrasVIP.setScaleEnabled(true);
            graficoBarrasVIP.setPinchZoom(true);

            // Animación
            graficoBarrasVIP.animateY(1000);
            graficoBarrasVIP.invalidate();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error en gráfico de barras vip: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}