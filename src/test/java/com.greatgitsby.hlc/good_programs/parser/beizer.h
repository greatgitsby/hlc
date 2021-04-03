begin
    variable p0;
    variable p1;
    variable p2;
    variable t;
    variable a1;
    variable a2;
    variable result;
    po := 0;
    p1 := 0 - 15;
    p2 := 10;
    t := 1 / 10;
    print "Distance: ";
    print t;
    print "\n";
    a1 := (p0 * (1 - t)) + (p1 * t);
    a2 := (p1 * (1 - t)) + (p2 * t);
    result := (a1 * (1 - t)) + (a2 * t);
    print "Interpolated value: ";
    print result;
    print "\n"
end
