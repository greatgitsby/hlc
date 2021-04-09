{ A simple program to test nested if}
 
begin
    variable u;

    u := 10;

    if u < 30 then
       if u < 10 then
          print "oops, incorrect!\n"
       else
          print "yup, correct!\n"
     else
        print "nope, incorrect!\n"
end
