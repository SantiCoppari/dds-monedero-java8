package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo = 0;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta(){
    setSaldo(0);
    setMovimientos(movimientos);
  }
  /*public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }CODE SMELL,se puede hacer un constructor de cuenta,que se inicialice con cero movimientos*/

  public void poner(double cuanto) {
    this.validarMontoNegativo(cuanto);
    /*if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }//CODE SMELL,podemos hacer un metodo validarMontoNegativo que se encargue de realizar la excepcion*/

    this.validar3DepositoriosDiarios();
    /*if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }//CODE SMELL,podemos abstraer la condicion del if, y hacer un metodo que retorno un booleano denominado validarDepositosDiarios*/

    //new Movimiento(LocalDate.now(), cuanto, true); CODE SMELL,aca utilizamos el metodo que agrega movimientos a nuestra cuenta
    this.agregarMovimiento(LocalDate.now(),cuanto,true);
    this.agregarSaldo(cuanto);
  }

  public void validarMontoNegativo(double cuanto){
    if(cuanto <= 0){
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
  }

  public void validar3DepositoriosDiarios(){
    if(this.getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3){
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
  }

  public void agregarMovimiento(LocalDate fecha, double cuanto, boolean esDeposito) {
    Movimiento movimiento = new Movimiento(fecha, cuanto, esDeposito);
    movimientos.add(movimiento);
  }

  public void agregarSaldo(double cuanto){
    saldo = this.getSaldo() + cuanto;
  }

  public void sacar(double cuanto) {
    this.validarMontoNegativo(cuanto);
    /*if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }//CODE SMELL,ya explicado*/
    this.validarDineroDisponible(cuanto);
    /*if (getSaldo() - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }//CODE SMELL,lo mismo para este tipo de excepciones*/
    this.validarExtraccionNoSupereLimite(cuanto);
    /*double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = 1000 - montoExtraidoHoy;
    if (cuanto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }*/
    //new Movimiento(LocalDate.now(), cuanto, false);CODE SMELL, agregamos movimiento de extraccion
    this.agregarMovimiento(LocalDate.now(),cuanto,false);
    this.retirarSaldo(cuanto);
  }

  public void validarDineroDisponible(double cuanto){
    if( this.getSaldo() - cuanto < 0){
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
  }

  public void retirarSaldo(double cuanto){
    saldo = getSaldo() - cuanto;
  }

  public void validarExtraccionNoSupereLimite(double cuanto){
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = 1000 - montoExtraidoHoy;
    if(cuanto > limite){
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
              + " diarios, límite: " + limite);
    }
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> movimiento.isExtraccion() && movimiento.esDeLaFecha(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }
}
