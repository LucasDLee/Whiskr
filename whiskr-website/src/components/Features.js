import { FeatureCard } from "./FeatureCard";
import mvvm from "../images/mvvm.png"
import threads from "../images/thread_diagram.png"

export const Features = () => {

  const featuresData = [
    {
      href: "https://youtu.be/tPzHgD19CP4",
      icon: (
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 26 26">
          <polygon className="play-btn__svg" points="9.33 6.69 9.33 19.39 19.3 13.04 9.33 6.69" fill="white" stroke="none" />
          <path className="play-btn__svg" d="M26,13A13,13,0,1,1,13,0,13,13,0,0,1,26,13ZM13,2.18A10.89,10.89,0,1,0,23.84,13.06,10.89,10.89,0,0,0,13,2.18Z" fill="white" stroke="none" />
        </svg>
      ),
      title: "Project Pitch",
      description: "See our project pitch which introduces our problem statement and solution, as well as the features we plan to implement!",
    },
    {
      href: "https://youtu.be/K6d81liuEe0",
      icon: (
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 26 26">
          <polygon className="play-btn__svg" points="9.33 6.69 9.33 19.39 19.3 13.04 9.33 6.69" fill="white" stroke="none" />
          <path className="play-btn__svg" d="M26,13A13,13,0,1,1,13,0,13,13,0,0,1,26,13ZM13,2.18A10.89,10.89,0,1,0,23.84,13.06,10.89,10.89,0,0,0,13,2.18Z" fill="white" stroke="none" />
        </svg>
      ),
      title: "Show and Tell 1",
      description: "In our first project update, we highlight some of the work we've completed so far. This includes a Figma mockup showcasing our user interface, a Model View Viewmodel visualizing our data flow, and a small demonstration of the app to name a few!",
    },
    {
      href: "https://youtu.be/yuF7flWvXCk?si=SpfqmU6Ac9UlzHJY",
      icon: (
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 26 26">
          <polygon className="play-btn__svg" points="9.33 6.69 9.33 19.39 19.3 13.04 9.33 6.69" fill="white" stroke="none" />
          <path className="play-btn__svg" d="M26,13A13,13,0,1,1,13,0,13,13,0,0,1,26,13ZM13,2.18A10.89,10.89,0,1,0,23.84,13.06,10.89,10.89,0,0,0,13,2.18Z" fill="white" stroke="none" />
        </svg>
      ),
      title: "Show and Tell 2",
      description: "In our second project update, we showcase some of the work we've completed so far on our application. This includes our Catbot using Botpress, a Firebase database to store our users, the adoption process and API, and finally the maps feature to find nearby cat-related businesses!",
    },
    {
      href: "https://youtu.be/PbIGqXxdjqE ",
      icon: (
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 26 26">
          <polygon className="play-btn__svg" points="9.33 6.69 9.33 19.39 19.3 13.04 9.33 6.69" fill="white" stroke="none" />
          <path className="play-btn__svg" d="M26,13A13,13,0,1,1,13,0,13,13,0,0,1,26,13ZM13,2.18A10.89,10.89,0,1,0,23.84,13.06,10.89,10.89,0,0,0,13,2.18Z" fill="white" stroke="none" />
        </svg>
      ),
      title: "Final Presentation",
      description: "In our final presentation, we go back over the premise of our idea, the challenges we faced, what we learned, and a quick demonstration of our application!",
    }
  ];
  

  const whoDidWhat = [
    {
        name: "Cira",
        tasks: [
            "Developed the CatBot interface, implemented authentication, and integrated chat storage with Firebase",
            "Created the random cat facts feature",
            "Contributed to user interface design"
        ]
    },
    {
        name: "Denise",
        tasks: [
            "Developed the CatMap feature using Google Maps and Places API",
            "Produced and edited presentation videos",
            "Contributed to user interface design"
        ]
    },
    {
        name: "Lucas",
        tasks: [
            "Trained the language model for cat-related queries in Botpress and integrated it into CatBot responses",
            "Created the project website",
            "Designed the MVVM architecture diagram"
        ]
    },
    {
        name: "Victor",
        tasks: [
            "Implemented the cat adoption listing feature using RescueGroups API",
            "Designed the thread diagram"
        ]
    }
  ]

  return (
    <section className="featuresSection">
      <div className="featureSeparator">
        <div className="featureHeader">Resources</div>
        <div className="featureSubheader">Learn More About the Progress of Whiskr</div>
        <div className="featureDescription">We've recorded our progress to demonstrate the full lifecycle of our groundbreaking cat adoption application.</div>
      </div>
      <div className="featureGrid">
        {featuresData.map((feature, key) => {
          const content = (
            <FeatureCard icon={feature.icon} title={feature.title}>
              {feature.description}
            </FeatureCard>
          );

          return feature.href ? (
            <a key={feature.id} href={feature.href} target="_blank" rel="noreferrer noopener">
              {content}
            </a>
          ) : (
            <div key={key}>{content}</div>
          );
        })}
      </div>
      <div className="diagramsSection">
        <div>
          <div className="featureSubheader">Model-View-ViewModel</div>
          <div className="featureDescription">A broad overview of how our app connects together.</div>
          <img src={mvvm} alt="mvvm" />
        </div>
        <div>
          <div className="featureSubheader">Thread Diagram</div>
          <div className="featureDescription">How we've set up our threaded calls to and from our APIs.</div>
          <img src={threads} alt="threads" />
        </div>
      </div>
      <div className="whoDidWhatSection featureSeparator">
        <div className="featureSubheader">Breakdown of Our Tasks</div>
        <div className="featureDescription">Here's a quick breakdown of our task allocation for each member.</div>
        <div className="teamTasks">
          {
            whoDidWhat.map((person, key) => {
              return (
                <div key={key} className="personTask">
                  <div className="teamCardName">{person.name}</div>
                  <ul className="teamCardDescription">
                    {person.tasks.map((task, key) => {
                      return <li key={key}>{task}</li>
                    })}
                  </ul>
                </div>
              )
            })
          }
        </div>
      </div>
    </section>
  );
};
