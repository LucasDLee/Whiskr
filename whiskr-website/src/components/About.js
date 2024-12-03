import { ResourceButton } from "./ResourceButton"
import githubIcon from "../images/github.webp"
import androidStudioIcon from "../images/android_studio_icon.png"

export const About = () => {

  const resources = [
    {
      description: "Want to see a demo of our app? Check out our APK!",
      download: true,
      icon: androidStudioIcon,
      link: "https://github.com/LucasDLee/Whiskr/releases/download/APK/Whiskr.apk",
      title: "Download our APK"
    },
    {
      description: "See the code in our repository",
      download: false,
      icon: githubIcon,
      link: "https://github.com/LucasDLee/Whiskr",
      title: "GitHub"
    },
    {
      description: "Our application is already packaged for you!",
      download: true,
      icon: androidStudioIcon,
      link: "https://github.com/LucasDLee/Whiskr/releases/download/ZIP/whiskr-app.zip",
      title: "ZIP File to our Application"
    }
  ]

  return (
    <section className="featuresSection">
      <div className="featureSeparator">
        <div className="featureHeader">About</div>
        <div className="featureSubheader">What is Whiskr?</div>
        <div className="featureDescription">Whiskr is a cat-adoption Android application where we've made it easier to adopt a cat.</div>
      </div>
      
      <div className="downloadButtons">
        {resources.map((resource, key) => {
          return <ResourceButton
                  key={key}
                  description={resource.description} 
                  download={resource.download}
                  icon={resource.icon}
                  link={resource.link}
                  title={resource.title} />
        })}
      </div>
    </section>
  )
}