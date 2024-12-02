# VISUAL TESTING

Repositorio de pruebas con Selenium Java y JUnit 5, Donde se definen las clases ComparatorCSSExt.java, que compara CSS de una página web para determinar si sufrió cambios o no y ComparatorHTML.java, que compara el HTML de la página web para determinar si se realizaron cambios en el HTML de la página; para poder simplificar su uso se crea una clase VisualComparator.java para manejar ambas clases, en la que se definen los métodos:

* savePage(): Guarda el HTML y CSS externos de la página actual en archivos denominados base con extensión html y css respectivamente.

* comparePage(): Compara el HTML y CSS base con el HTML y CSS de la página actual, y muestra por terminal si hay discrepancias entre estos.

* modifyBase(): Modifica los archivos HTML y CSS bases por el HTML y CSS de la página actual.
