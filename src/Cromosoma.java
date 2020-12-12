public class Cromosoma {

    private int anchoTablero = 0;

    private int genes[];

    private double fitness = 0.0;

    private boolean seleccionado = false;

    private double probSeleccion = 0.0;

    private int conflictos = 0;

    /**
     * Constructor del la clase
     */
    public Cromosoma(int longitud) {

        this.anchoTablero = longitud;
        genes = new int[longitud];

        for (int i = 0; i < longitud; i++) {
            this.genes[i] = i;
        }
    }

    /**
     * Calcula los conflictos de movimiento entre las reinas
     */
    public void CalcularConflictos() {
        int x = 0;
        int y = 0;
        int auxx = 0;
        int auxy = 0;
        String tablero[][] = new String[anchoTablero][anchoTablero];
        int numConflictos = 0;
        int dx[] = new int[]{-1, 1, -1, 1};
        int dy[] = new int[]{-1, 1, 1, -1};
        boolean terminado = false;

        // Limpia el tablero.
        for (int i = 0; i < anchoTablero; i++) {
            for (int j = 0; j < anchoTablero; j++) {
                tablero[i][j] = "";
            }
        }

        for (int i = 0; i < anchoTablero; i++) {
            tablero[i][this.genes[i]] = "Q";
        }

        // Recorrer cada una de las Reinas y calcular el número de conflictos.
        for (int i = 0; i < anchoTablero; i++) {
            x = i;
            y = this.genes[i];

            // Evaluar diagonales.
            for (int j = 0; j <= 3; j++) {
                auxx = x;
                auxy = y;
                terminado = false;
                while (!terminado) {
                    auxx += dx[j];
                    auxy += dy[j];
                    if ((auxx < 0 || auxx >= anchoTablero) || (auxy < 0 || auxy >= anchoTablero)) {
                        terminado = true;
                    } else {
                        if (tablero[auxx][auxy].compareToIgnoreCase("Q") == 0) {
                            numConflictos++;
                        }
                    }
                }
            }
        }

        this.conflictos = numConflictos;
    }

    /**
     * Coloca conflicto
     */
    public void setConflictos(int value) {
        this.conflictos = value;
    }

    /**
     * Obtiene conflicto
     *
     * @return int
     */
    public int getConflictos() {
        return this.conflictos;
    }

    /**
     * Obtiene la probabilidad de seleccion
     *
     * @return double
     */
    public double getProbSeleccion() {
        return probSeleccion;
    }

    /**
     * Coloca la probabilidad de seleccion
     */
    public void setProbSeleccion(double SelProb) {
        probSeleccion = SelProb;
    }

    /**
     * Obtiene si esta seleccionado
     *
     * @return boolean
     */
    public boolean getSeleccionado() {
        return seleccionado;
    }

    /**
     * Selecciona el cromosoma
     */
    public void setSeleccionado(boolean sValue) {
        seleccionado = sValue;
    }

    /**
     * Obtiene el grado de optitud del cromosoma
     *
     * @return double
     */
    public double getFitness() {
        return fitness;
    }

    /**
     * Coloca el grado de aptitud del cromosoma
     */
    public void SetFitness(double score) {
        fitness = score;
    }

    /**
     * Obtiene los genes del cromosoma
     *
     * @return int
     */
    public int getGenesByIndex(int index) {
        return genes[index];
    }

    /**
     * Coloca los genes al cromosoma
     */
    public void setGenes(int index, int value) {
        genes[index] = value;
    }

}

