{
This is the formula for a 1-dimensional quadratic bezier curve
Takes 3 values and a 'time' input from 0-1
}
begin
    { Inputs }
    variable p0;
    variable p1;
    variable p2;
    variable t;

    { Temporaries }
    variable a1;
    variable a2;

    { Output }
    variable result;

    { Set up example values for the inputs }
    po := 0;
    p1 := 0 - 15;
    p2 := 10;
    t := 1 / 10;
    print "Distance: ";
    print t;
    print "\n";

    { Compute the two first-order interpolations }
    a1 := (p0 * (1 - t)) + (p1 * t);
    a2 := (p1 * (1 - t)) + (p2 * t);

    { Compute the second-order interpolation }
    result := (a1 * (1 - t)) + (a2 * t);

    { Print the result }
    print "Interpolated value: ";
    print result;
    print "\n"
end