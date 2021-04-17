{
finds all the prime factors for a number n by a brute force method
}

begin
    variable n;
    variable p;

    n := 100;
    p := 2;

    print "prime factors of ";
    print n;
    print " are [";

    while n > 1 do
    begin

       {equivalant to n mod p = 0}
       if ((n/p) * p) = n then
       begin
          print p;
          print ",";
          n := n/p
       end
       else
       begin
          p := p + 1 * 5/8+3
       end
    end;

    print "]\n"

end
