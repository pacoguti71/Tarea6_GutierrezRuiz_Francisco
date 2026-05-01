package dam.pmdm.tarea6_gutierrezruiz_francisco

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dam.pmdm.tarea6_gutierrezruiz_francisco.databinding.ActivityMainBinding
import dam.pmdm.tarea6_gutierrezruiz_francisco.databinding.DialogoBinding
import dam.pmdm.tarea6_gutierrezruiz_francisco.datos.DatosMapa
import dam.pmdm.tarea6_gutierrezruiz_francisco.datos.Mision

/**
 * Actividad principal de la aplicación que gestiona el mapa interactivo de misiones.
 *
 * Esta clase se encarga de:
 * - Inicializar y configurar Google Maps con un estilo personalizado.
 * - Gestionar permisos de ubicación en tiempo real.
 * - Ubicar marcadores dinámicos que representan puntos de control o misiones.
 * - Permitir al usuario interactuar con las misiones mediante diálogos de validación de códigos.
 * - Controlar la visualización de la ubicación actual del usuario mediante un interruptor (Switch).
 * - Proporcionar controles de navegación para restablecer la vista panorámica de todos los marcadores.
 *
 * Implementa [OnMapReadyCallback] para gestionar la carga del mapa de forma asíncrona.
 *
 * @author Francisco Gutiérrez Ruiz
 */
class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    // Constantes
    private val LOCATION_REQUEST_CODE = 100 // Código de solicitud para permisos de ubicación. Debe ser un valor único para identificar esta solicitud en el callback de permisos.
    private val MARGEN_ZOOM = 120f // Margen en píxeles para el ajuste de zoom al mostrar los marcadores. Determina cuánto espacio adicional se dejará cuando se ajuste la cámara para mostrarlos todos.

    // Estado
    private lateinit var binding: ActivityMainBinding // ViewBinding de la actividad
    private lateinit var miMapa: GoogleMap // Instancia del mapa de Google
    private lateinit var limites: LatLngBounds // Límites geográficos que engloban todos los marcadores
    private var marcadorUsuario: Marker? = null

    /**
     * Punto de entrada principal de la actividad.
     *
     * Se encarga de inicializar la interfaz de usuario, gestionar los permisos de ubicación
     * necesarios, cargar el fragmento del mapa de Google y configurar los componentes
     * interactivos de la pantalla.
     *
     * @param savedInstanceState Si la actividad se está reinstalando después de haber sido
     * cerrada previamente, este bundle contiene los datos que suministró más recientemente.
     */
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        configurarPantalla()
        solicitarPermisoLocalizacion()
        inicializarMapa()
        configurarBotonResetYSwitch()
    }

    // -------------------------
    // CONFIGURACIÓN UI
    // -------------------------
    /**
     * Configura la interfaz de usuario de la actividad.
     *
     * Realiza la inicialización del View Binding, habilita el modo de pantalla completa (edge-to-edge)
     * y ajusta los márgenes (padding) de la vista principal para que el contenido se adapte correctamente
     * a las barras del sistema (estado y navegación).
     */
    private fun configurarPantalla() {
        // Configura la actividad para que el contenido se muestre detrás de las barras del sistema (status bar y navigation bar)
        enableEdgeToEdge()
        // Infla el layout usando View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        // Establece el contenido de la actividad al layout inflado
        setContentView(binding.root)
        // Configura un listener para aplicar los insets de las barras del sistema como padding al contenedor principal
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // -------------------------
    // PERMISOS
    // -------------------------
    /**
     * Comprueba si el permiso de ubicación precisa ha sido concedido por el usuario.
     * Si no dispone del permiso, solicita tanto la ubicación precisa como la aproximada
     * utilizando el código de solicitud [LOCATION_REQUEST_CODE].
     */
    private fun solicitarPermisoLocalizacion() {
        // Verifica si el permiso de ubicación ya ha sido concedido por el usuario antes de solicitarlo
        if (!tienePermisosUbicacion()) {
            // Si el permiso no ha sido concedido, solicita al usuario que lo otorgue
            requestPermissions(
                // Solicita ambos permisos de ubicación: ACCESS_FINE_LOCATION para ubicación precisa y ACCESS_COARSE_LOCATION para ubicación aproximada
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_REQUEST_CODE
            )
        }
    }

    /**
     * Comprueba si el usuario ha otorgado el permiso de acceso a la ubicación precisa
     * (ACCESS_FINE_LOCATION) para la aplicación.
     *
     * @return `true` si el permiso ha sido concedido; `false` en caso contrario.
     */
    private fun tienePermisosUbicacion(): Boolean {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // -------------------------
    // MAPA
    // -------------------------
    /**
     * Obtiene el fragmento del mapa de Google definido en el layout XML y registra la actividad
     * como callback para ser notificada cuando el mapa esté listo para su uso.
     */
    private fun inicializarMapa() {
        // Obtiene el fragmento del mapa definido en el layout XML
        val mapaFragmento = supportFragmentManager.findFragmentById(R.id.mapa) as SupportMapFragment
        // Registra la actividad como callback cuando el mapa esté listo
        mapaFragmento.getMapAsync(this)
    }

    /**
     * Se ejecuta cuando el mapa de Google está listo para ser usado.
     *
     * Inicializa el mapa, añade marcadores, calcula los límites para encuadrarlos,
     * ajusta el zoom inicial y configura la interacción con los marcadores.
     */
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onMapReady(googleMap: GoogleMap) {
        val estiloMapa = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapa_style)
        miMapa = googleMap
        miMapa.setMapStyle(estiloMapa)

        incluirMarcadores()
        mostrarUbicacionUsuario()
        ajustarZoomAMarcadores()
        configurarEventosMapa()
    }

    /**
     * Callback que recibe el resultado de la solicitud de permisos de ubicación.
     *
     * Si el usuario concede los permisos (especificados por [LOCATION_REQUEST_CODE]), se procede a
     * habilitar la visualización de la posición del usuario en el mapa. En caso contrario, se
     * notifica al usuario que la funcionalidad de ubicación no estará disponible.
     *
     * @param requestCode El código de solicitud pasado en [requestPermissions].
     * @param permissions El array de permisos solicitados.
     * @param grantResults El resultado de la concesión para los permisos correspondientes.
     */
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Verifica si el resultado corresponde a la solicitud de permisos de ubicación
        if (requestCode == LOCATION_REQUEST_CODE) {
            // Si el usuario ha concedido el permiso, habilita la funcionalidad relacionada con la ubicación
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
                Toast.makeText(this, "Permiso de ubicación concedido", Toast.LENGTH_SHORT).show()
                mostrarUbicacionUsuario()
            } else {
                // Permiso denegado
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Gestiona la visualización de la ubicación actual del usuario en el mapa.
     *
     * Si los permisos de ubicación están concedidos y el interruptor de seguimiento está activado,
     * obtiene la última localización conocida, coloca o actualiza un marcador personalizado
     * (Astrobot) en dicha posición y desplaza la cámara hacia ella.
     *
     * En caso de que el interruptor esté desactivado o no se disponga de permisos,
     * elimina el marcador del usuario del mapa si este existiera.
     */
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun mostrarUbicacionUsuario() {
        // Verifica si el permiso de ubicación ha sido concedido y si el switch para mostrar la ubicación está activado
        if (tienePermisosUbicacion() && binding.switch1.isChecked) {
            // Obtiene el cliente de ubicación fusionada para acceder a la ubicación del dispositivo
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            // Solicita la última ubicación conocida del dispositivo y añade un listener para manejar el resultado
            fusedLocationClient.lastLocation.addOnSuccessListener { localizacion ->
                // Si se ha obtenido una ubicación válida, actualiza o crea el marcador de la ubicación del usuario en el mapa
                if (localizacion != null) {
                    // Crea un objeto LatLng con la latitud y longitud de la ubicación obtenida
                    val ubicacionUsuario = LatLng(localizacion.latitude, localizacion.longitude)
                    // Si el marcador de la ubicación del usuario no existe, créalo
                    if (marcadorUsuario == null) {
                        // Crea un nuevo marcador en la ubicación del usuario con un título y un icono personalizado
                        marcadorUsuario = miMapa.addMarker(
                            MarkerOptions()
                                .position(ubicacionUsuario)
                                .title("Tu ubicación")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_astrobot))
                        )
                    } else {
                        // Si el marcador ya existe, actualiza su posición a la nueva ubicación del usuario
                        marcadorUsuario?.position = ubicacionUsuario
                    }
                    // Anima la cámara para centrar la vista en la ubicación del usuario con un nivel de zoom específico
                    miMapa.animateCamera(CameraUpdateFactory.newLatLngZoom(ubicacionUsuario, 12f))
                }
            }
        } else {
            // Eliminar marcador si el switch está apagado o no hay permiso
            marcadorUsuario?.remove()
            marcadorUsuario = null
        }
    }

    /**
     * Añade los marcadores al mapa a partir de la lista de localizaciones predefinidas.
     *
     * Recorre la colección de datos, crea un marcador para cada ubicación y lo posiciona en el mapa.
     * Simultáneamente, calcula los límites geográficos ([LatLngBounds]) que engloban todos los
     * puntos para permitir el encuadre posterior de la cámara.
     */
    private fun incluirMarcadores() {
        // Crea un constructor de límites para calcular el área que abarca todos los marcadores
        val limitesMapa = LatLngBounds.builder()
        // Recorre cada localización definida en los datos
        for (localizacion in DatosMapa.localizaciones) {
            // Crea un objeto LatLng con la latitud y longitud de la localización
            val posicion = LatLng(localizacion.latitud, localizacion.longitud)
            // Incluye esta posición en el cálculo de los límites del mapa
            limitesMapa.include(posicion)
            // Añade un marcador al mapa en la posición especificada con el título correspondiente
            val marcador = miMapa.addMarker(
                MarkerOptions()
                    .position(posicion)
                    .title(localizacion.titulo)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            )
            // Asocia la misión correspondiente a este marcador usando el campo tag para acceder a ella posteriormente
            marcador?.tag = localizacion.mision
        }
        // Construye el objeto LatLngBounds final a partir de las posiciones incluidas, que representará el área que abarca todos los marcadores
        limites = limitesMapa.build()
    }

    /**
     * Configura los eventos de interacción del mapa.
     *
     * Define el comportamiento al pulsar sobre un marcador: anima la cámara para centrar la vista
     * en la posición del marcador con un nivel de zoom específico y despliega su ventana
     * de información (InfoWindow).
     */
    private fun configurarEventosMapa() {
        // Configura un listener para el evento de clic en los marcadores del mapa
        miMapa.setOnMarkerClickListener { marcador ->
            // Obtiene la misión asociada al marcador a través de su campo tag, si es que existe
            val mision = marcador.tag as? Mision
            // Si no se ha hecho el zoom, anima la cámara para acercarse al marcador
            if (miMapa.cameraPosition.zoom < 12f) {
                miMapa.animateCamera(CameraUpdateFactory.newLatLngZoom(marcador.position, 12f))
                marcador.showInfoWindow()
            } else {
                // Si ya está cerca, muestra la misión
                if (mision != null) {
                    mostrarDialogoMision(mision, marcador)
                }
            }
            // Devuelve true para indicar que el evento de clic ha sido manejado y evitar el comportamiento predeterminado
            true
        }
    }

    /**
     * Muestra un diálogo interactivo para realizar la misión asociada a un marcador.
     *
     * El diálogo utiliza un layout personalizado que permite al usuario visualizar el enunciado
     * de la misión e introducir un código de validación. Si el código es correcto, la misión
     * se marca como completada y el color del marcador cambia a verde.
     *
     * El diálogo se posiciona en la parte superior de la pantalla para mantener la visibilidad del mapa.
     *
     * @param mision El objeto [Mision] que contiene los datos de la prueba (enunciado, código, estado).
     * @param marcador El objeto [Marker] de Google Maps vinculado a la misión para actualizar su apariencia.
     */
    private fun mostrarDialogoMision(mision: Mision, marcador: Marker) {
        // Infla el layout personalizado
        val dialogBinding = DialogoBinding.inflate(layoutInflater)
        // Establece el enunciado de la misión en el TextView del diálogo
        dialogBinding.tvEnunciado.text = mision.enunciado
        // Prellena el EditText del diálogo con el código correcto de la misión para facilitar las pruebas, aunque en una aplicación real esto no se haría para evitar que el usuario vea la respuesta correcta
        dialogBinding.etCodigo.setText(mision.codigoMisionCompletada)
        // Crea un cuadro de diálogo utilizando MaterialAlertDialogBuilder, configurando su título, icono, vista personalizada y botones de acción
        val cuadroDialogo = com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle(mision.puntoControl)
            .setIcon(R.drawable.ic_astrobot)
            .setView(dialogBinding.root) // Usamos el root del binding
            .setPositiveButton("Comprobar", null)
            .setNegativeButton("Cerrar", null)
            .create()
        // Configura un listener para cuando el diálogo se muestre, para gestionar la habilitación del botón de comprobación y la lógica de validación del código introducido
        cuadroDialogo.setOnShowListener {
            // Obtiene el botón de comprobación del diálogo
            val botonComprobar = cuadroDialogo.getButton(AlertDialog.BUTTON_POSITIVE)
            // Inicialmente, el botón de comprobación está deshabilitado hasta que el usuario introduzca algo en el campo de texto
            botonComprobar.isEnabled = false
            // Configura un listener para detectar cambios en el campo de texto
            dialogBinding.etCodigo.doOnTextChanged { text, _, _, _ ->
                // Habilita el botón de comprobación solo si el campo de texto no está vacío o en blanco
                botonComprobar.isEnabled = !text.isNullOrBlank()
            }
            // Configura el comportamiento del botón de comprobación al hacer clic
            botonComprobar.setOnClickListener {
                // Obtiene el código introducido por el usuario, eliminando espacios en blanco al inicio y al final
                val codigoIntroducido = dialogBinding.etCodigo.text.toString().trim()
                // Compara el código introducido con el código correcto de la misión para determinar si se ha completado
                if (codigoIntroducido == mision.codigoMisionCompletada) {
                    // Si el código es correcto, marca la misión como completada
                    mision.completada = true
                    // Cambia el color del marcador a verde para indicar que la misión ha sido completada
                    marcador.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    // Muestra un mensaje de éxito al usuario indicando que la misión ha sido completada
                    Toast.makeText(this, "Misión completada", Toast.LENGTH_SHORT).show()
                    // Cierra el diálogo después de completar la misión
                    cuadroDialogo.dismiss()
                    // Si el código es incorrecto, muestra un mensaje de error al usuario indicando que el código no es correcto y que la misión no se ha completado
                } else {
                    Toast.makeText(this, "Código incorrecto. Misión no completada.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        // Muestra el diálogo al usuario y ajusta su posición para que aparezca en la parte superior de la pantalla con un ancho que ocupe todo el espacio disponible y una altura que se ajuste al contenido
        cuadroDialogo.show()
        cuadroDialogo.window?.apply {
            setGravity(Gravity.TOP)
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    // -------------------------
    // BOTÓN
    // -------------------------
    /**
     * Configura el comportamiento del botón de reset del zoom.
     * Al pulsar el botón, si los límites de los marcadores están inicializados,
     * la cámara del mapa se reajusta para mostrar todos los puntos de interés.
     */
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun configurarBotonResetYSwitch() {
        binding.botonResetZoom.setOnClickListener {
            if (::limites.isInitialized) {
                ajustarZoomAMarcadores()
            }
        }

        binding.switch1.setOnCheckedChangeListener { _, _ ->
            if (tienePermisosUbicacion() && this::miMapa.isInitialized) {
                mostrarUbicacionUsuario()
            }
        }
    }

    // -------------------------
    // ZOOM
    // -------------------------
    /**
     * Ajusta la cámara del mapa para que todos los marcadores definidos en [limites] sean visibles.
     */
    private fun ajustarZoomAMarcadores() {
        miMapa.animateCamera(
            CameraUpdateFactory.newLatLngBounds(limites, MARGEN_ZOOM.toInt())
        )
    }
}