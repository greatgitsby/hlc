{
This is Euclid's method for computing the 
Greatest Common Divisor
}
 
begin
    variable u;
    variable v;
    variable t;

    u := 30;
    v := 102;

    while u > 0 do
    begin
       if u < v then
       begin
          t := u;
          u := v;
          v := t
       end;
       u := u - v
    end;

    print "gcd: ";
    print v;
    print "\n"

end





asdf;