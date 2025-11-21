package com.app.backend_app.config;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.app.backend_app.models.Categoria;
import com.app.backend_app.models.Especialidad;
import com.app.backend_app.models.Plato;
import com.app.backend_app.models.Usuario;
import com.app.backend_app.repository.CategoriaRepository;
import com.app.backend_app.repository.EspecialidadRepository;
import com.app.backend_app.repository.PlatoRepository;
import com.app.backend_app.repository.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final EspecialidadRepository especialidadRepository;
    private final UsuarioRepository usuarioRepository;
    private final PlatoRepository platoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        inicializarCategorias();
        inicializarEspecialidades();
        inicializarUsuarios();
        inicializarPlatos();
        
        System.out.println("✅ Datos de prueba inicializados correctamente");
    }

    private void inicializarCategorias() {
        if (categoriaRepository.count() == 0) {
            List<Categoria> categorias = Arrays.asList(
                Categoria.builder()
                    .nombre("Entradas")
                    .descripcion("Platos para comenzar tu experiencia gastronómica")
                    .imagenUrl("/images/categorias/entradas.jpg")
                    .orden(1)
                    .activa(true)
                    .build(),
                    
                Categoria.builder()
                    .nombre("Platos de Fondo")
                    .descripcion("Nuestros platos principales y especialidades")
                    .imagenUrl("/images/categorias/platos-fondo.jpg")
                    .orden(2)
                    .activa(true)
                    .build(),
                    
                Categoria.builder()
                    .nombre("Postres")
                    .descripcion("Dulces delicias para terminar tu comida")
                    .imagenUrl("/images/categorias/postres.jpg")
                    .orden(3)
                    .activa(true)
                    .build()
            );

            categoriaRepository.saveAll(categorias);
            System.out.println("📁 Categorías creadas: " + categorias.size());
        }
    }

    private void inicializarEspecialidades() {
        if (especialidadRepository.count() == 0) {
            List<Especialidad> especialidades = Arrays.asList(
                Especialidad.builder()
                    .nombre("Criolla")
                    .descripcion("Auténtica comida tradicional peruana")
                    .imagenUrl("/images/especialidades/criolla.jpg")
                    .activa(true)
                    .build(),
                    
                Especialidad.builder()
                    .nombre("Marina")
                    .descripcion("Frescos platos con mariscos y pescados")
                    .imagenUrl("/images/especialidades/marina.jpg")
                    .activa(true)
                    .build(),
                    
                Especialidad.builder()
                    .nombre("Parrillas")
                    .descripcion("Carnes a la parrilla y asados")
                    .imagenUrl("/images/especialidades/parrillas.jpg")
                    .activa(true)
                    .build()
            );

            especialidadRepository.saveAll(especialidades);
            System.out.println("🎯 Especialidades creadas: " + especialidades.size());
        }
    }

    private void inicializarUsuarios() {
        if (!usuarioRepository.existsByEmail("admin@softeat.com")) {
            Usuario admin = Usuario.builder()
                .nombre("Administrador")
                .apellido("SoftEat")
                .email("admin@softeat.com")
                .password(passwordEncoder.encode("12345"))
                .telefono("+51987654321")
                .rol(Usuario.Rol.ADMINISTRADOR)
                .activo(true)
                .build();

            usuarioRepository.save(admin);
            System.out.println("👤 Usuario administrador creado: admin@softeat.com / 12345");
        }
    }

    private void inicializarPlatos() {
        if (platoRepository.count() == 0) {
            // Obtener categorías y especialidades
            Categoria entradas = categoriaRepository.findByNombre("Entradas").get();
            Categoria platosFondo = categoriaRepository.findByNombre("Platos de Fondo").get();
            Categoria postres = categoriaRepository.findByNombre("Postres").get();

            Especialidad criolla = especialidadRepository.findByNombre("Criolla").get();
            Especialidad marina = especialidadRepository.findByNombre("Marina").get();

            List<Plato> platos = Arrays.asList(
                Plato.builder()
                    .nombre("Lomo Saltado")
                    .descripcion("Clásico peruano con lomo de res, cebolla, tomate y papas fritas")
                    .precio(new BigDecimal("32.00"))
                    .categoria(platosFondo)
                    .especialidad(criolla)
                    .imagenUrl("/images/platos/lomo-saltado.jpg")
                    .disponible(true)
                    .esDestacado(true)
                    .tiempoPreparacion(25)
                    .rangoPrecio(Plato.RangoPrecio.MEDIO)
                    .build(),

                Plato.builder()
                    .nombre("Ceviche Clásico")
                    .descripcion("Pescado fresco marinado en limón, cebolla y ají limo")
                    .precio(new BigDecimal("28.00"))
                    .categoria(entradas)
                    .especialidad(marina)
                    .imagenUrl("/images/platos/ceviche.jpg")
                    .disponible(true)
                    .esDestacado(true)
                    .tiempoPreparacion(15)
                    .rangoPrecio(Plato.RangoPrecio.MEDIO)
                    .build(),

                Plato.builder()
                    .nombre("Ají de Gallina")
                    .descripcion("Pollo desmenuzado en cremosa salsa de ají amarillo")
                    .precio(new BigDecimal("26.00"))
                    .categoria(platosFondo)
                    .especialidad(criolla)
                    .imagenUrl("/images/platos/aji-gallina.jpg")
                    .disponible(true)
                    .esDestacado(false)
                    .tiempoPreparacion(30)
                    .rangoPrecio(Plato.RangoPrecio.ECONOMICO)
                    .build()
            );

            platoRepository.saveAll(platos);
            System.out.println("🍽️ Platos de ejemplo creados: " + platos.size());
        }
    }
}