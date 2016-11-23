package br.com.jordan.cadeopenha.util;

/**
 * Created by Michael on 06/11/2016.
 */

public class Constantes {

    private Constantes(){}

    /** ALERT **/
    public static String ALERT_INFORMACAO = "Informação";
    public static String ALERT_AVISO = "Aviso";
    public static String ALERT_ALERTA = "Alerta";
    public static String ALERT_SUCESSO = "Sucesso";
    public static String ALERT_ERRO = "Erro";
    public static String ALERT_SIM = "Sim";
    public static String ALERT_NAO = "Não";
    public static String ALERT_OK = "Ok";
    public static String ALERT_CANCELAR = "Cancelar";

    public static final String GCM_SERVER_ID = "226174978215";
    public static final String GCM_IS_NOTIFICATION = "IS_NOTIFICATION";
    public static final String GCM_REGISTRATION_ID_TCC = "REGISTRATION_ID_TCC";
    public static final String GCM_APP_TCC_CODE = "br.fiap.tcc.notification";
    public static final String GCM_PLATAFORMA = "1";

    public static final String WS_CONTENT_TYPE = "Content-Type";
    public static final String WS_APPLICATION_JSON = "application/json";
    public static final String WS_ACCEPT = "Accept";
    public static final String WS_CONTENT_TYPE_TEXT = "text/json";

    public static final String REGISTERED_IN_BACK_END = "REGISTERED_IN_BACK_END";
    //	public static final String PROPERTY_REG_ID = "REGISTRATION_ID_TCC";
    public static final String REGISTERED_IN_GCM = "REGISTERED_IN_GCM";
    public static final String MSG_SERVICE_NOT_AVAILABLE = "Servico do Google Register nao disponivel";

    public static final String TCC_SHARED_PREFS = "TCC_SHARED_PREFS";
    public static final String SHOW_INTRO_PREFS_KEY = "SHOW_INTRO_PREFS_KEY";

    public static final String REQUEST_METHOD_GET = "GET";
    public static final String REQUEST_METHOD_POST = "POST";
    public static final String REQUEST_PROPERTY_AUTHORIZATION = "Authorization";
    public static final String REQUEST_PROPERTY_CONTENT_LENGHT = "Content-Length";
    public static final int READ_TIMEOUT = 10000;
    public static final int CONNECT_TIMEOUT = 15000;


    public static final String PROJECT_NUMBER = "226174978215";
    public static final String TAG = "GCMClientManager";
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String MSG_WAIT = "Aguarde, buscando informações";
    public static final String URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";

    public static final String URL_REGISTER_DEVICE_WS = "http://www.alarmap.com.br:9080/tcc-notification/rest/service/register";
    public static final String ACTION_PROXIMITY_ALERT = "br.com.jordan.cadeopenha.ACTION_PROXIMITY_ALERT";
}
