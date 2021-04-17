{
  Tim Goodfellow;
  CSIS480;
  Spring 2013;
  Generates Pascal's Triangle;
  Written more for keeping my compiler honest than being *elegant* code;
  Change numberOfLevels to print more or less rows;
}

begin
  { Delcare our variables }
  variable doWhileVariableOuter;
  variable doWhileVariableInner;
  variable variableToPrint;
  variable numberOfLevels;

  { Initialize variables }
  doWhileVariableOuter := 0;
  doWhileVariableInner := 0;
  numberOfLevels := 20;

  begin
    { Outer loop, one per each level }
    while doWhileVariableOuter < numberOfLevels do
    begin

      { Set the initial value to 1 }
      variableToPrint := 1;

      { Inner loop, runs n times for the nth level }
      while doWhileVariableInner <= doWhileVariableOuter do
        begin

          { Print the value and a space }
          print variableToPrint;
          print " ";

          { "Increment" our value }
          variableToPrint := (variableToPrint*(doWhileVariableOuter-doWhileVariableInner))/(doWhileVariableInner+1);

          { Increment the counter for the inner loop }
          doWhileVariableInner := doWhileVariableInner + 1
        end;

      { Print a new line }
      print "\n";

      { Increment the counter for the outer loop and reinitialize the inner loop counter }
      doWhileVariableOuter := doWhileVariableOuter + 1;
      doWhileVariableInner := 0
    end
  end
end
