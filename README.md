Construir el POST para algún servicio que tengan en su repositorio de universitas o proyecto de clase.
El servicio debe respetar los principios de servicio rest, por lo cual debe ser una petición consumida por el método POST.
En el servicio se debe validar si el correo existe, en caso de existir debe devolver el usuario existente y en el código de estatus http se debe enviar un 200 (consultar la siguiente fuente) . En caso de no existir, se debe insertar y se debe devolver el usuario insertado con todos sus datos (incluyendo el id, el cual debe recuperarse mediante algún mecanismo que tenga java para hacerlo, por lo cual NO se debe consultar el usuario insertado), una vez insertado, se debe enviar el código de estatus http como 201.
La respuesta enviada al cliente en cualquier debe ser un JSON, por lo cual debe ser indispensable el uso de GSON, para este fin.
El ejercicio se debe enviar al Slack antes del Lunes 13 de Mayo a media noche.
En caso de enviar el ejercicio, solo se recibe el repositorio en GitHub.

Notas:
Interaccion la base de datos estan en la clase ConexionMySQL
Servlet con POST está en UserServlet, se borro dopost que ya existia para realizar la actividad
