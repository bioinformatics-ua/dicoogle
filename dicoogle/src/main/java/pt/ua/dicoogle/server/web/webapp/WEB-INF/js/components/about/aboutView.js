var React = require('react');
var AboutView = React.createClass({
    componentDidMount: function() {
      $.ajax({

        url: "http://localhost:8080/login",
        dataType: 'json',
        success: function(data) {
          console.log(data);

        },
        error: function(xhr, status, err) {
          console.log("not loggedin");
          $.post("http://localhost:8080/login",
          {
            username: "dicoogle",
            password: "dicoogle"
          },
            function(data, status){
              //Response
              console.log("Data: " + data + "\nStatus: " + status);
            });
        }
      });

      var bilo =  sessionStorage.getItem("session_id")==null?"ERAAAA UMA VEZ":sessionStorage.getItem("session_id");
      console.log(bilo);

		  sessionStorage.setItem("session_id","uma pussara");

      console.log(document.cookie);

    },
      render: function() {
        return ( <div>
          Lorem ipsum dolor sit amet, ei novum percipitur quo, patrioque consequuntur at vim.His everti regione alterum ex.Ei qui ancillae intellegat theophrastus, deleniti salutatus id ius, dolor insolens indoctum duo ut.Ne utinam diceret pro, in sit eleifend facilisis concludaturque, ne corrumpit rationibus definitiones his.Te vidisse virtute docendi est, mei ea exerci adversarium.

          An sea iusto putent posidonium.Quod nusquam in est, cibo erat habeo ut vis, ad melius albucius fabellas eam.Eu quodsi omnesque voluptaria sea, his essent voluptaria an.Consul legimus voluptua no ius, verear interesset has ea, mel cu prima insolens gubergren.Mea meis dolore postulant ad.

          Impetus vivendo vulputate cum ad, duo luptatum intellegebat at, duo ut quot mucius dolores.Meis ignota facilisis id sed.Ei est forensibus temporibus deterruisset.Ex solum pericula est, ipsum doctus repudiare cu eum.In evertitur temporibus definitiones eam, exerci oportere conclusionemque an vim.

          Cu per timeam rationibus, ea vidit eligendi antiopam vel.Agam animal contentiones ex nec, propriae probatus sadipscing et sea.Cu mei fugit integre.Iusto affert nam cu, ullum simul recusabo ei pro, ut munere aperiam disputationi eos. </div>);
        }
      });

    export {
      AboutView
    }
