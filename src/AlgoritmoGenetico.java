import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AlgoritmoGenetico {
    private static final int poblacionInicial = 75;           // TamaÃ±o de la poblacion
    private static final double probApareamiento = 0.7;   // Probabilidad de reproduccion entre dos cromosomas. rango: 0.0 < probMutacion < 1.0
    private static final double tasaMutacion = 0.001;     // Tasa de mutacion. rango: 0.0 < tasaMutacion < 1.0
    private static final int minSeleccion = 10;           // Minimo de padres para la seleccion.
    private static final int maxSeleccion = 50;           // Maximo de padres para la seleccion. rango: minSeleccion < maxSeleccion < poblacionIni
    private static final int numDesendencia = 20;         // Cantidad desendencia por generacion. rango: 0 < numDesendencia < maxSeleccion.
    private static final int minBaraja = 8;               // Rango para generar aleatorios
    private static final int maxBaraja = 20;
    private static final int ptsCruce = 4;                // Maximo de puntos de cruce. rango: 0 < ptsCruce < 8
    private static final int anchoTablero = 8;              // Ancho del tablero.

    private static int generacion = 0;
    private static int numHijos = 0;
    private static int sigMutacion = 0;             // Programa las mutaciones.
    private static int numMutaciones = 0;
    private static List<Cromosoma> poblacion  = new ArrayList<>();

    public static void main(String[] args) {
        int tamanioPoblacion = 0;
        Cromosoma cromosoma = null;
        boolean terminado = false;
        GenerarPoblacionInicial();                          //Genera población inicial

        numMutaciones = 0;
        sigMutacion = NumeroAleatorio(0, (int) Math.round(1.0 / tasaMutacion));

        while (!terminado) {                                //Mientras no terminada la búsqueda
            tamanioPoblacion = poblacion.size();            //Tamaño de población
            for (int i = 0; i < tamanioPoblacion; i++) {
                cromosoma = poblacion.get(i);
                if ((cromosoma.getConflictos() == 0)) {
                    terminado = true;
                }
            }

            Fitness();                                      //Calcula el fitness dependiendo la cantidad de conflictos

            Seleccion();                                    //Selecciona los padres de la siguiente generación

            Reproduccion();                                 //Realiza el cruce parcial de los padres

            PrepararSiguienteGeneracion();                  //Selecciona los hijos de la siguiente generación

            generacion++;
        }

        tamanioPoblacion = poblacion.size();
        for (int i = 0; i < tamanioPoblacion; i++) {
            cromosoma = poblacion.get(i);
            if (cromosoma.getConflictos() == 0) {
                ImprimirSolucion(cromosoma);
            }
        }
        System.out.println("Resuelto en " + generacion + " generaciones \nencontrado con " + numMutaciones + " mutaciones \nen el " + numHijos + " cromosoma.");
    }

    /**
     * Busca la mejor actitud
     */
    private static void Fitness() {
        // Menor error = 100%, Mayor Error = 0%
        int tamanioPoblacion = poblacion.size();
        Cromosoma cromosoma = null;
        double mejor = 0;
        double peor = 0;

        // La peor puntuacion seria el que tiene la mayor energia, mejor mas bajo.
        peor = poblacion.get(Maximo()).getConflictos();

        // Convertir a un porcentaje ponderado.
        mejor = peor - poblacion.get(Minimo()).getConflictos();

        for (int i = 0; i < tamanioPoblacion; i++) {
            cromosoma = poblacion.get(i);
            cromosoma.SetFitness((peor - cromosoma.getConflictos()) * 100.0 / mejor);
        }
    }

    /**
     * Seleccion de cromosomas de la generacion
     */
    private static void Seleccion() {

        int j = 0;
        int tamanioPoblacion = poblacion.size();
        double genTotal = 0.0;
        double selTotal = 0.0;
        int maximoSeleccionar = NumeroAleatorio(minSeleccion, maxSeleccion);
        double seleccionar = 0.0;
        Cromosoma cromosoma = null;
        Cromosoma comosomaAux = null;
        boolean terminado = false;

        for (int i = 0; i < tamanioPoblacion; i++) {
            cromosoma = poblacion.get(i);
            genTotal += cromosoma.getFitness();
        }

        genTotal *= 0.01;

        for (int i = 0; i < tamanioPoblacion; i++) {
            cromosoma = poblacion.get(i);
            cromosoma.setProbSeleccion(cromosoma.getFitness() / genTotal);
        }

        for (int i = 0; i < maximoSeleccionar; i++) {
            seleccionar = NumeroAleatorio(0, 99);
            j = 0;
            selTotal = 0;
            terminado = false;
            while (!terminado) {
                cromosoma = poblacion.get(j);
                selTotal += cromosoma.getProbSeleccion();
                if (selTotal >= seleccionar) {
                    if (j == 0) {
                        comosomaAux = poblacion.get(j);
                    } else if (j >= tamanioPoblacion - 1) {
                        comosomaAux = poblacion.get(tamanioPoblacion - 1);
                    } else {
                        comosomaAux = poblacion.get(j - 1);
                    }
                    comosomaAux.setSeleccionado(true);
                    terminado = true;
                } else {
                    j++;
                }
            }
        }
    }

    /**
     * Produce una nueva generacion
     */
    private static void Reproduccion() {
        int getRand = 0;
        int padreA = 0;
        int padreB = 0;
        int newIndex1 = 0;
        int newIndex2 = 0;
        Cromosoma newChromo1 = null;
        Cromosoma newChromo2 = null;

        for (int i = 0; i < numDesendencia; i++) {
            padreA = SeleccionarPadre();
            // Probabilidad de prueba de reproduccion.
            getRand = NumeroAleatorio(0, 100);
            if (getRand <= probApareamiento * 100) {
                padreB = SeleccionarPadre(padreA);
                newChromo1 = new Cromosoma(anchoTablero);
                newChromo2 = new Cromosoma(anchoTablero);
                poblacion.add(newChromo1);
                newIndex1 = poblacion.indexOf(newChromo1);
                poblacion.add(newChromo2);
                newIndex2 = poblacion.indexOf(newChromo2);

                // Elige uno o ambos de los siguientes:
                CruceParcial(padreA, padreB, newIndex1, newIndex2);

                if (numHijos - 1 == sigMutacion) {
                    IntercambiarMutacion(newIndex1, 1);
                } else if (numHijos == sigMutacion) {
                    IntercambiarMutacion(newIndex2, 1);
                }

                poblacion.get(newIndex1).CalcularConflictos();
                poblacion.get(newIndex2).CalcularConflictos();

                numHijos += 2;

                // Programa la siguiente mutacion.
                if (numHijos % (int) Math.round(1.0 / tasaMutacion) == 0) {
                    sigMutacion = numHijos + NumeroAleatorio(0, (int) Math.round(1.0 / tasaMutacion));
                }
            }
        } // i
    }

    /**
     * Cruza con probabilidad dos individuos obteniendo dos decendientes
     *
     * @param chromA
     * @param chromB
     * @param hijo1
     * @param hijo2
     */
    private static void CruceParcial(int chromA, int chromB, int hijo1, int hijo2) {
        int j = 0;
        int item1 = 0;
        int item2 = 0;
        int pos1 = 0;
        int pos2 = 0;
        Cromosoma cromosoma = poblacion.get(chromA);
        Cromosoma comosomaAux = poblacion.get(chromB);
        Cromosoma newChromo1 = poblacion.get(hijo1);
        Cromosoma newChromo2 = poblacion.get(hijo2);
        int crossPoint1 = NumeroAleatorio(0, anchoTablero - 1);
        int crossPoint2 = NumeroAleatorioExclusivo(anchoTablero - 1, crossPoint1);

        if (crossPoint2 < crossPoint1) {
            j = crossPoint1;
            crossPoint1 = crossPoint2;
            crossPoint2 = j;
        }

        // Copia los genes de padres a hijos.
        for (int i = 0; i < anchoTablero; i++) {
            newChromo1.setGenes(i, cromosoma.getGenesByIndex(i));
            newChromo2.setGenes(i, comosomaAux.getGenesByIndex(i));
        }

        for (int i = crossPoint1; i <= crossPoint2; i++) {
            // Obtener los dos elementos que intercambian.
            item1 = cromosoma.getGenesByIndex(i);
            item2 = comosomaAux.getGenesByIndex(i);

            // Obtiene los items, posiciones en la descendencia.
            for (j = 0; j < anchoTablero; j++) {
                if (newChromo1.getGenesByIndex(j) == item1) {
                    pos1 = j;
                } else if (newChromo1.getGenesByIndex(j) == item2) {
                    pos2 = j;
                }
            } // j

            // Intercambiar.
            if (item1 != item2) {
                newChromo1.setGenes(pos1, item2);
                newChromo1.setGenes(pos2, item1);
            }

            // Obtiene los items, posiciones en la descendencia.
            for (j = 0; j < anchoTablero; j++) {
                if (newChromo2.getGenesByIndex(j) == item2) {
                    pos1 = j;
                } else if (newChromo2.getGenesByIndex(j) == item1) {
                    pos2 = j;
                }
            } // j

            // Intercambiar.
            if (item1 != item2) {
                newChromo2.setGenes(pos1, item1);
                newChromo2.setGenes(pos2, item2);
            }

        } // i
    }

    /**
     * Cruza con probabilidad dos individuos obteniendo dos decendientes
     *
     * @param chromA
     * @param chromB
     * @param hijo1
     * @param hijo2
     */
    private static void CrucePorPosicion(int chromA, int chromB, int hijo1, int hijo2) {
        int k = 0;
        int numPoints = 0;
        int tempArray1[] = new int[anchoTablero];
        int tempArray2[] = new int[anchoTablero];
        boolean matchFound = false;
        Cromosoma cromosoma = poblacion.get(chromA);
        Cromosoma comosomaAux = poblacion.get(chromB);
        Cromosoma newChromo1 = poblacion.get(hijo1);
        Cromosoma newChromo2 = poblacion.get(hijo2);

        // Elegir y ordenar los puntos de cruce.
        numPoints = NumeroAleatorio(0, ptsCruce);
        int crossPoints[] = new int[numPoints];
        for (int i = 0; i < numPoints; i++) {
            crossPoints[i] = NumeroAleatorio(0, anchoTablero - 1, crossPoints);
        } // i

        // Obtenga no elegidos de los padres 2
        k = 0;
        for (int i = 0; i < anchoTablero; i++) {
            matchFound = false;
            for (int j = 0; j < numPoints; j++) {
                if (comosomaAux.getGenesByIndex(i) == cromosoma.getGenesByIndex(crossPoints[j])) {
                    matchFound = true;
                }
            } // j
            if (matchFound == false) {
                tempArray1[k] = comosomaAux.getGenesByIndex(i);
                k++;
            }
        } // i

        // Insertar elegido al hijo 1.
        for (int i = 0; i < numPoints; i++) {
            newChromo1.setGenes(crossPoints[i], cromosoma.getGenesByIndex(crossPoints[i]));
        }

        // Rellene no elegidos para hijos 1.
        k = 0;
        for (int i = 0; i < anchoTablero; i++) {
            matchFound = false;
            for (int j = 0; j < numPoints; j++) {
                if (i == crossPoints[j]) {
                    matchFound = true;
                }
            } // j
            if (matchFound == false) {
                newChromo1.setGenes(i, tempArray1[k]);
                k++;
            }
        } // i

        // Obtenga no elegidos de los padres 1
        k = 0;
        for (int i = 0; i < anchoTablero; i++) {
            matchFound = false;
            for (int j = 0; j < numPoints; j++) {
                if (cromosoma.getGenesByIndex(i) == comosomaAux.getGenesByIndex(crossPoints[j])) {
                    matchFound = true;
                }
            } // j
            if (matchFound == false) {
                tempArray2[k] = cromosoma.getGenesByIndex(i);
                k++;
            }
        } // i

        // Inserte elegido en hijos 2.
        for (int i = 0; i < numPoints; i++) {
            newChromo2.setGenes(crossPoints[i], comosomaAux.getGenesByIndex(crossPoints[i]));
        }

        // Rellene no elegidos para hijos 2.
        k = 0;
        for (int i = 0; i < anchoTablero; i++) {
            matchFound = false;
            for (int j = 0; j < numPoints; j++) {
                if (i == crossPoints[j]) {
                    matchFound = true;
                }
            } // j
            if (matchFound == false) {
                newChromo2.setGenes(i, tempArray2[k]);
                k++;
            }
        } // i
    }

    /**
     * Intercambia los genes, realiza la mutacion
     *
     * @param indice
     * @param intercambio
     */
    private static void IntercambiarMutacion(int indice, int intercambio) {
        int i = 0;
        int tempData = 0;
        Cromosoma cromosoma = null;
        int gene1 = 0;
        int gene2 = 0;
        boolean terminado = false;

        cromosoma = poblacion.get(indice);

        while (!terminado) {
            gene1 = NumeroAleatorio(0, anchoTablero - 1);
            gene2 = NumeroAleatorioExclusivo(anchoTablero - 1, gene1);

            // Cambia los genes seleccionados.
            tempData = cromosoma.getGenesByIndex(gene1);
            cromosoma.setGenes(gene1, cromosoma.getGenesByIndex(gene2));
            cromosoma.setGenes(gene2, tempData);

            if (i == intercambio) {
                terminado = true;
            }
            i++;
        }
        numMutaciones++;
    }

    /**
     * Seleccionar los padres para la reproduccion
     *
     * @return
     */
    private static int SeleccionarPadre() {
        // FunciÃ³n sobrecargada, consulta "choosepadre (ByVal Parenta As Integer)".
        int padre = 0;
        Cromosoma cromosoma = null;
        boolean terminado = false;

        while (!terminado) {
            // Elige al azar un padre elegible.
            padre = NumeroAleatorio(0, poblacion.size() - 1);
            cromosoma = poblacion.get(padre);
            if (cromosoma.getSeleccionado() == true) {
                terminado = true;
            }
        }

        return padre;
    }

    /**
     * Seleccionar los padres para la reproduccion, diferente al seleccionado
     *
     * @return int
     */
    private static int SeleccionarPadre(int padreA) {
        // FunciÃ³n sobrecargada, consulta "choosepadre()".
        int padre = 0;
        Cromosoma cromosoma = null;
        boolean terminado = false;

        while (!terminado) {
            // Elige al azar un padre elegible.
            padre = NumeroAleatorio(0, poblacion.size() - 1);
            if (padre != padreA) {
                cromosoma = poblacion.get(padre);
                if (cromosoma.getSeleccionado() == true) {
                    terminado = true;
                }
            }
        }

        return padre;
    }

    /**
     * Prepara la poblacion de la siguiente generacion
     */
    private static void PrepararSiguienteGeneracion() {
        int tamanioPoblacion = 0;
        Cromosoma cromosoma = null;

        // Restaura estado de cromosoma
        tamanioPoblacion = poblacion.size();
        for (int i = 0; i < tamanioPoblacion; i++) {
            cromosoma = poblacion.get(i);
            cromosoma.setSeleccionado(false);
        }
    }

    /**
     * Imprime la mejor solucion
     *
     * @param mejorSolucion
     */
    private static void ImprimirSolucion(Cromosoma mejorSolucion) {
        String tablero[][] = new String[anchoTablero][anchoTablero];

        // Limpia el tablero.
        for (int x = 0; x < anchoTablero; x++) {
            for (int y = 0; y < anchoTablero; y++) {
                tablero[x][y] = "";
            }
        }

        for (int x = 0; x < anchoTablero; x++) {
            tablero[x][mejorSolucion.getGenesByIndex(x)] = "Q";
        }

        // Muestra el tablero.
        for (int y = 0; y < anchoTablero; y++) {
            for (int x = 0; x < anchoTablero; x++) {
                if (tablero[x][y] == "Q") {
                    System.out.print("X ");
                } else {
                    System.out.print("O ");
                }
            }
            System.out.println();
        }

        System.out.println();
        System.out.println();
    }

    /**
     * Obtiene un numero aleatorio en el rango
     *
     * @param low
     * @param high
     * @return
     */
    private static int NumeroAleatorio(int low, int high) {
        return (int) Math.round((high - low) * new Random().nextDouble() + low);
    }

    /**
     *
     * @param high
     * @param except
     * @return
     */
    private static int NumeroAleatorioExclusivo(int high, int except) {
        boolean terminado = false;
        int getRand = 0;

        while (!terminado) {
            getRand = new Random().nextInt(high);
            if (getRand != except) {
                terminado = true;
            }
        }

        return getRand;
    }

    /**
     * Obtener numero aleatorio fuera del rango
     *
     * @param low
     * @param high
     * @param except
     * @return
     */
    private static int NumeroAleatorio(int low, int high, int[] except) {
        boolean terminado = false;
        int getRand = 0;

        if (high != low) {
            while (!terminado) {
                terminado = true;
                getRand = (int) Math.round((high - low) * new Random().nextDouble() + low);
                for (int i = 0; i < except.length; i++) //UBound(except)
                {
                    if (getRand == except[i]) {
                        terminado = false;
                    }
                } // i
            }
            return getRand;
        } else {
            return high; // or low (it doesn't matter).
        }
    }

    /**
     * Obtiene el minimo cromosoma
     *
     * @return int
     */
    private static int Minimo() {
        // Devuelve un Ã­ndice de matriz.
        int tamanioPoblacion = 0;
        Cromosoma cromosoma = null;
        Cromosoma comosomaAux = null;
        int winner = 0;
        boolean foundNewWinner = false;
        boolean terminado = false;

        while (!terminado) {
            foundNewWinner = false;
            tamanioPoblacion = poblacion.size();
            for (int i = 0; i < tamanioPoblacion; i++) {
                if (i != winner) {             // Avoid self-comparison.
                    cromosoma = poblacion.get(i);
                    comosomaAux = poblacion.get(winner);
                    if (cromosoma.getConflictos() < comosomaAux.getConflictos()) {
                        winner = i;
                        foundNewWinner = true;
                    }
                }
            }
            if (foundNewWinner == false) {
                terminado = true;
            }
        }
        return winner;
    }

    /**
     * Obtiene el maximo cromosoma
     *
     * @return
     */
    private static int Maximo() {
        // Devuelve un Indice de matriz.
        int tamanioPoblacion = 0;
        Cromosoma cromosoma = null;
        Cromosoma comosomaAux = null;
        int winner = 0;
        boolean foundNewWinner = false;
        boolean terminado = false;

        while (!terminado) {
            foundNewWinner = false;
            tamanioPoblacion = poblacion.size();
            for (int i = 0; i < tamanioPoblacion; i++) {
                if (i != winner) {             // Avoid self-comparison.
                    cromosoma = poblacion.get(i);
                    comosomaAux = poblacion.get(winner);
                    if (cromosoma.getConflictos() > comosomaAux.getConflictos()) {
                        winner = i;
                        foundNewWinner = true;
                    }
                }
            }
            if (foundNewWinner == false) {
                terminado = true;
            }
        }
        return winner;
    }

    /**
     * Generar poblacion inicial
     */
    private static void GenerarPoblacionInicial() {
        int shuffles = 0;
        Cromosoma newChromo = null;
        int chromoIndex = 0;

        for (int i = 0; i < poblacionInicial; i++) {
            newChromo = new Cromosoma(anchoTablero);
            poblacion.add(newChromo);
            chromoIndex = poblacion.indexOf(newChromo);

            // Escoja al azar el numero de baraja realizar.
            shuffles = NumeroAleatorio(minBaraja, maxBaraja);

            IntercambiarMutacion(chromoIndex, shuffles);

            poblacion.get(chromoIndex).CalcularConflictos();

        }
    }
}
