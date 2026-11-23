package factory_method;

import java.lang.reflect.InvocationTargetException;


import evolutionary_algorithms.complement.FatherSelection;
import evolutionary_algorithms.complement.SelectionType;
import factory_interface.IFFactoryFatherSelection;




/** Factoría que crea operadores de selección de padres a partir de su tipo. */
public class FactoryFatherSelection implements IFFactoryFatherSelection{
    /** El operador de selección. */
    private FatherSelection selection;
	
    /**
     * Crea el operador de selección de padres indicado.
     *
     * @param selectionType tipo de selección
     * @return el operador de selección
     * @throws IllegalArgumentException si algún argumento no es válido
     * @throws SecurityException si el gestor de seguridad impide la reflexión
     * @throws ClassNotFoundException si la clase pedida a la factoría no existe o no está permitida
     * @throws InstantiationException si la clase pedida a la factoría no se puede instanciar
     * @throws IllegalAccessException si el constructor de la clase no es accesible
     * @throws InvocationTargetException si el constructor de la clase lanza una excepción
     * @throws NoSuchMethodException si la clase no tiene constructor sin argumentos
     */
    public FatherSelection createSelectFather(SelectionType selectionType) throws IllegalArgumentException, SecurityException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    	String className = "evolutionary_algorithms.complement." + selectionType.toString();
		selection = (FatherSelection) FactoryLoader.getInstance(className);
		return selection;
	}
}
