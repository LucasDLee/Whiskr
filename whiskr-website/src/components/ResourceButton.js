export const ResourceButton = (props) => {
  return (
    <a href={props.link} className="resourceButton" target="_blank" rel="noreferrer" download={props.download}>
      <img src={props.icon} alt={props.icon} className="resourceIcon" />
      <div>
        <div className="resourceTitle">{ props.title }</div>
        <div className="resourceDescription">{ props.description }</div>
      </div>
    </a>
  )
}