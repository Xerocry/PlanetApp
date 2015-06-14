package com.xerocry.planetapp;

/**
 * Created by Xerocry on 08.06.2015.
 */
public class FleetProcessor extends Thread{
    private static int delta = 1;
    private Game game;
    private Planet destination;
    private Fleet fleet;

    public FleetProcessor(Game game, Fleet fleet) {
        this.destination = fleet.getDestination();
        this.fleet = fleet;
        this.game = game;
    }

    @Override
    public void run() {
        while (Math.pow(destination.X - fleet.X, 2) +
                Math.pow(destination.Y - fleet.Y, 2) >= Math.pow(destination.RADIUS, 2)) {
            double cosin = Math.abs((destination.Y - fleet.Y) /
                    (Math.sqrt(Math.pow((destination.X - fleet.X), 2) +
                            Math.pow((destination.Y - fleet.Y), 2))));
            double yDelta = delta * cosin, xDelta = Math.sqrt(Math.pow(delta, 2) - Math.pow(yDelta, 2));
            if (fleet.X > destination.X)
                fleet.X = (fleet.X - xDelta);
            else if (fleet.X < destination.X)
                fleet.X = (fleet.X + xDelta);
            if (fleet.Y > destination.Y)
                fleet.Y = (fleet.Y - yDelta);
            else if (fleet.Y < destination.Y)
                fleet.Y = (fleet.Y + yDelta);
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
        }
        if(destination.owner == fleet.getOwner().owner)
        destination.numUnits+=fleet.shipAmount;
        else if (destination.numUnits <= fleet.shipAmount) {
            fleet.shipAmount = fleet.shipAmount - destination.numUnits;
            destination.numUnits =0;
            destination.numUnits+= fleet.shipAmount;
            destination.owner = fleet.getOwner().owner;
        }else{
            destination.numUnits-= fleet.shipAmount;
        }
        fleet.setArrived(true);

        game.stop(this);
    }
}
