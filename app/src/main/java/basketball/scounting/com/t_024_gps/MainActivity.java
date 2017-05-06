package basketball.scounting.com.t_024_gps;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button btn_gps;
    private Button btn_rede;
    private Button btn_parar;
    private Button btn_lbs;
    private Button btn_teste_conexao;

    private TextView tv_latitude;
    private TextView tv_logitude;

    private PegaTrouxaListener pegaTrouxaListener;

    private double latitude;
    private double longitude;

    private LocationManager lm;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.telainicial);

        inicializarVariavel();

        inicializarAcao();

        //executa um ação
        pegaTrouxaListener = new PegaTrouxaListener();

        //filtro
        IntentFilter filter = new IntentFilter("android.location.PROVIDERS_CHANGED");

        registerReceiver(pegaTrouxaListener, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(pegaTrouxaListener);
        super.onDestroy();
    }

    private void inicializarAcao() {
        btn_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limparCampos();
                habilitarDesabilitarBotoes(false);

                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        0,
                        0,
                        gpsListener);
            }
        });

        btn_rede.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limparCampos();
                habilitarDesabilitarBotoes(false);

                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        0,
                        0,
                        gpsListener);
            }
        });

        btn_parar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lm.removeUpdates(gpsListener);
                habilitarDesabilitarBotoes(true);

                String uri  ="geo:0,0?q=" + String.valueOf(latitude).replace(",",".") +"," + String.valueOf(longitude).replace(",",".");

                Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

                startActivity(mIntent);
            }
        });

        btn_lbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(TELECOM_SERVICE);

                GsmCellLocation location = (GsmCellLocation) tm.getCellLocation();

                StringBuilder sb = new StringBuilder();

                sb
                        .append("CELL ID : ")
                        .append(location.getCid())
                        .append("\n LAC: ")
                        .append(location.getLac())
                        .append("\n\n EXtra:")
                        .append(tm.getDeviceId());

                Toast.makeText(context,sb.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        btn_teste_conexao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkService()){
                    Toast.makeText(context,"Serviço ok", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder alerta= new AlertDialog.Builder(MainActivity.this);
                    alerta.setTitle("Serviço de GPS");
                    alerta.setMessage("Serviço Desligado. DEseja reativar?");
                    alerta.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent mIntent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS
                            );

                            startActivity(mIntent);
                        }
                    });
                    alerta.setNegativeButton("Não", null);
                    alerta.show();
                }
            }
        });
    }

    private LocationListener  gpsListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            latitude = location.getLatitude();
            tv_latitude.setText(String.valueOf(latitude));

            longitude = location.getLongitude();
            tv_logitude.setText(String.valueOf(longitude));

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private boolean checkService(){
        if(lm != null && (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) )){
            return  true;
        } else {
            return false;
        }
    }

    private void habilitarDesabilitarBotoes(boolean status) {

        btn_gps.setEnabled(status);
        btn_rede.setEnabled(status);
    }

    private void limparCampos() {

        tv_latitude.setText("");
        tv_logitude.setText("");
        
    }

    private void inicializarVariavel() {

        context = getBaseContext();
        
        lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        
        btn_gps = (Button) findViewById(R.id.btn_gps);
        btn_rede = (Button) findViewById(R.id.btn_rede);
        btn_parar = (Button) findViewById(R.id.btn_parar);
        btn_lbs = (Button) findViewById(R.id.btn_lbs);
        btn_teste_conexao = (Button) findViewById(R.id.btn_teste_conexao);

        tv_latitude = (TextView) findViewById(R.id.tv_latitude);
        tv_logitude = (TextView) findViewById(R.id.tv_logitude);
    }

    private class  PegaTrouxaListener extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String resultado = null;

            if (checkService()){
                resultado = "Serviço funcionando";
            } else {
                resultado = "Serviço parado";
            }

            Toast.makeText(context,resultado,Toast.LENGTH_LONG).show();
        }
    }
}
